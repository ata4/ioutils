/*
 ** 2014 Januar 29
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import static java.nio.channels.FileChannel.MapMode.*;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to read and write files with arbitrary size using paged memory mapping.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFile implements DataInput, DataOutput {
    
    public static final int PAGE_SIZE = Integer.MAX_VALUE;
    
    private List<MappedByteBuffer> buffers;
    private long position = 0;
    private long capacity;
    private ByteOrder order;
    private boolean readOnly;
    private ByteBuffer vbuf = ByteBuffer.allocate(8);
    
    public MemoryMappedFile(Path file, boolean readOnly, long ofs, long len) throws IOException {
        OpenOption[] openOptions = readOnly ? new OpenOption[] {READ} : new OpenOption[] {CREATE, READ, WRITE};
        try (FileChannel fc = FileChannel.open(file, openOptions)) {
            init(fc, readOnly, ofs, len);
        }
    }
    
    public MemoryMappedFile(Path file, boolean readOnly) throws IOException {
        this(file, readOnly, 0, Files.size(file));
    }
    
    public MemoryMappedFile(Path file) throws IOException {
        this(file, true);
    }
    
    private void init(FileChannel fc, boolean readOnly, long ofs, long len) throws IOException {
        int pages = (int) (len / PAGE_SIZE) + 1;
        long bufOfs = ofs;
        buffers = new ArrayList<>(pages);

        MapMode mapMode = readOnly ? READ_ONLY : READ_WRITE;
        for (int i = 0; i < pages; i++) {
            int bufLen = (int) Math.min(PAGE_SIZE, len - bufOfs);
            buffers.add(fc.map(mapMode, bufOfs, bufLen));
            bufOfs += bufLen;
        }
        
        this.readOnly = readOnly;
        this.capacity = len;
    }
    
    private MappedByteBuffer current() {
        int page = (int) (position / PAGE_SIZE);
        int pos = (int) (position % PAGE_SIZE);
        MappedByteBuffer bb = buffers.get(page);
        if (bb.position() != pos) {
            bb.position(pos);
        }
        return bb;
    }
    
    private void checkOverflow(long len) {
        if (len > remaining()) {
            throw new BufferOverflowException();
        }
    }
    
    private void checkUnderflow(long len) {
        if (len > remaining()) {
            throw new BufferUnderflowException();
        }
    }

    public void position(long newPosition) {
        if (newPosition < 0 || newPosition > capacity) {
            throw new IllegalArgumentException();
        }
        position = newPosition;
    }

    public long position() {
        return position;
    }

    public long capacity() {
        return capacity;
    }

    public long remaining() {
        return capacity - position;
    }

    public boolean hasRemaining() {
        return remaining() > 0;
    }
    
    public void order(ByteOrder bo) {
        for (MappedByteBuffer buffer : buffers) {
            buffer.order(bo);
        }
        vbuf.order(bo);
        order = bo;
    }

    public ByteOrder order() {
        return order;
    }
    
    public boolean isReadOnly() {
        return readOnly;
    }
    
    public byte get() {
        checkUnderflow(1);
        byte b = current().get();
        position++;
        return b;
    }
    
    public void get(byte[] dst, int offset, int length) {
        checkUnderflow(length);
        
        ByteBuffer bb = current();
        int remaining = bb.remaining();
        int len2 = Math.min(length, remaining);

        bb.get(dst, offset, len2);
        position += len2;
        
        if (len2 != length) {
            bb = current();
            bb.get(dst, offset + len2, length - len2);
            position += length - len2;
        }
    }
    
    public void get(byte[] dst) {
        get(dst, 0, dst.length);
    }
    
    public void get(ByteBuffer dst) {
        ByteBuffer src = current();
        
        int remainingSrc = src.remaining();
        int remainingDst = dst.remaining();
        
        checkUnderflow(remainingDst);
        
        if (remainingSrc >= remainingDst) {
            // read from page directly
            dst.put(src);
        } else {
            // read from first page
            int remaining = remainingSrc;
            for (int i = 0; i < remaining; i++) {
                dst.put(src.get());
                position++;
            }
            
            // read from second page
            remaining = remainingDst - remainingSrc;
            src = current();
            for (int i = 0; i < remaining; i++) {
                dst.put(src.get());
                position++;
            }
        }
    }
    
    public void put(byte dst) {
        checkOverflow(1);
        current().put(dst);
        position++;
    }
    
    public void put(byte[] src, int offset, int length) {
        checkOverflow(length);
        
        ByteBuffer bb = current();
        int remaining = bb.remaining();
        int len2 = Math.min(length, remaining);

        bb.put(src, offset, len2);
        position += len2;
        
        if (len2 != length) {
            bb = current();
            bb.put(src, offset + len2, length - len2);
            position += length - len2;
        }
    }
    
    public void put(byte[] src) {
        put(src, 0, src.length);
    }
    
    public void put(ByteBuffer src) {
        ByteBuffer dst = current();
        
        int remainingSrc = src.remaining();
        int remainingDst = dst.remaining();
        
        checkOverflow(remainingSrc);
        
        if (remainingDst >= remainingSrc) {
            // write to page directly
            dst.put(src);
        } else {
            // write to first page
            int remaining = remainingDst;
            for (int i = 0; i < remaining; i++) {
                dst.put(src.get());
                position++;
            }
            
            // write to second page
            remaining = remainingSrc - remainingDst;
            dst = current();
            for (int i = 0; i < remaining; i++) {
                dst.put(src.get());
                position++;
            }
        }
    }
    
    private ByteBuffer getValueBuffer(int len) {
        vbuf.limit(len);
        vbuf.rewind();
        get(vbuf);
        vbuf.rewind();
        return vbuf;
    }
    
    private ByteBuffer getValueBufferWrite(int len) {
        vbuf.limit(len);
        vbuf.rewind();
        return vbuf;
    }
    
    private void putValueBuffer() {
        vbuf.flip();
        put(vbuf);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        try {
            get(b);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        try {
            get(b, off, len);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        long rem = remaining();
        if (rem < Integer.MAX_VALUE) {
            n = Math.min(n, (int) rem);
        }
        position += n;
        return n;
    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return get() == 1;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return get();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        try {
            return get() & 0xff;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public short readShort() throws IOException {
        try {
            ByteBuffer bb = current();
            short v;
            final int len = 2;
            if (bb.remaining() >= len) {
                v = bb.getShort();
                position += len;
            } else {
                v = getValueBuffer(len).getShort();
            }
            return v;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    @Override
    public char readChar() throws IOException {
        try {
            ByteBuffer bb = current();
            char v;
            final int len = 2;
            if (bb.remaining() >= len) {
                v = bb.getChar();
                position += len;
            } else {
                v = getValueBuffer(len).getChar();
            }
            return v;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            ByteBuffer bb = current();
            int v;
            final int len = 4;
            if (bb.remaining() >= len) {
                v = bb.getInt();
                position += len;
            } else {
                v = getValueBuffer(len).getInt();
            }
            return v;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            ByteBuffer bb = current();
            long v;
            final int len = 8;
            if (bb.remaining() >= len) {
                v = bb.getLong();
                position += len;
            } else {
                v = getValueBuffer(len).getLong();
            }
            return v;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public float readFloat() throws IOException {
        try {
            ByteBuffer bb = current();
            float v;
            final int len = 4;
            if (bb.remaining() >= len) {
                v = bb.getFloat();
                position += len;
            } else {
                v = getValueBuffer(len).getFloat();
            }
            return v;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            ByteBuffer bb = current();
            double v;
            final int len = 8;
            if (bb.remaining() >= len) {
                v = bb.getDouble();
                position += len;
            } else {
                v = getValueBuffer(len).getDouble();
            }
            return v;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public String readLine() throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            for (byte c = 0; hasRemaining() && c != '\n'; c = get()) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    @Override
    public void write(int b) throws IOException {
        try {
            put((byte) (b & 0xff));
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            put(b);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            put(b, off, len);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        try {
            write(v ? (byte) 1 : (byte) 0);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeByte(int v) throws IOException {
        try {
            put((byte) (v & 0xff));
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        try {
            ByteBuffer bb = current();
            short value = (short) (v & 0xffff);
            final int len = 2;
            if (bb.remaining() >= len) {
                bb.putShort(value);
                position += len;
            } else {
                getValueBufferWrite(len).putShort(value);
                putValueBuffer();
            }
        } catch (BufferOverflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try {
            ByteBuffer bb = current();
            char value = (char) (v & 0xffff);
            final int len = 2;
            if (bb.remaining() >= len) {
                bb.putChar((char) (v & 0xffff));
                position += len;
            } else {
                getValueBufferWrite(len).putChar(value);
                putValueBuffer();
            }
        } catch (BufferOverflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try {
            ByteBuffer bb = current();
            final int len = 4;
            if (bb.remaining() >= len) {
                bb.putInt(v);
                position += len;
            } else {
                getValueBufferWrite(len).putInt(v);
                putValueBuffer();
            }
        } catch (BufferOverflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try {
            ByteBuffer bb = current();
            final int len = 8;
            if (bb.remaining() >= len) {
                bb.putLong(v);
                position += len;
            } else {
                getValueBufferWrite(len).putLong(v);
                putValueBuffer();
            }
        } catch (BufferOverflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        try {
            ByteBuffer bb = current();
            final int len = 4;
            if (bb.remaining() >= len) {
                bb.putFloat(v);
                position += len;
            } else {
                getValueBufferWrite(len).putFloat(v);
                putValueBuffer();
            }
        } catch (BufferOverflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        try {
            ByteBuffer bb = current();
            final int len = 8;
            if (bb.remaining() >= len) {
                bb.putDouble(v);
                position += len;
            } else {
                getValueBufferWrite(len).putDouble(v);
                putValueBuffer();
            }
        } catch (BufferOverflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void writeBytes(String s) throws IOException {
        write(s.getBytes());
    }

    @Override
    public void writeChars(String s) throws IOException {
        try {
            final int len = s.length();
            for (int i = 0; i < len; i++) {
                writeChar(s.charAt(i));
            }
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeUTF(String s) throws IOException {
        write(s.getBytes("UTF-8"));
    }
}
