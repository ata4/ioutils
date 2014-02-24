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

import info.ata4.io.SeekOrigin;
import info.ata4.io.Seekable;
import info.ata4.io.Swappable;
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
public class MemoryMappedFile implements Swappable, Seekable {
    
    private static final int PAGE_SIZE = Integer.MAX_VALUE;
    
    private List<MappedByteBuffer> buffers;
    private long position = 0;
    private long capacity;
    private ByteOrder order;
    private boolean readOnly;
    private boolean swap;
    
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
            buffers.set(i, fc.map(mapMode, bufOfs, bufLen));
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
    
    @Override
    public boolean isSwap() {
        return swap;
    }

    @Override
    public void setSwap(boolean swap) {
        this.swap = swap;
        for (ByteBuffer buffer : buffers) {
            buffer.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
    }
    
    @Override
    public void seek(long where, SeekOrigin whence) throws IOException {
        long pos = 0;
        switch (whence) {
            case BEGINNING:
                pos = where;
                break;
            
            case CURRENT:
                pos = position() + where;
                break;
                
            case END:
                pos = capacity() - where;
                break;
        }
        position(pos);
    }

    @Override
    public void position(long newPosition) throws IOException {
        if (newPosition > capacity) {
            throw new IllegalArgumentException();
        }
        position = newPosition;
    }

    @Override
    public long position() throws IOException {
        return position;
    }

    @Override
    public long capacity() throws IOException {
        return capacity;
    }

    @Override
    public long remaining() {
        return capacity - position;
    }

    @Override
    public boolean hasRemaining() {
        return remaining() > 0;
    }
    
    public void order(ByteOrder bo) {
        for (MappedByteBuffer buffer : buffers) {
            buffer.order(bo);
        }
        this.order = bo;
    }

    public ByteOrder order() {
        return order;
    }
    
    public boolean isReadOnly() {
        return readOnly;
    }
    
    public byte get() {
        byte b = current().get();
        position++;
        return b;
    }
    
    public void get(byte[] dst, int offset, int length) {
        if (remaining() < length) {
            throw new BufferUnderflowException();
        }
        
        MappedByteBuffer bb = current();
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
        MappedByteBuffer src = current();
        
        int remainingSrc = src.remaining();
        int remainingDst = dst.remaining();
        
        if (remaining() < remainingDst) {
            throw new BufferUnderflowException();
        }
        
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
        current().put(dst);
        position++;
    }
    
    public void put(byte[] src, int offset, int length) {
        if (remaining() < length) {
            throw new BufferOverflowException();
        }
        
        MappedByteBuffer bb = current();
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
        MappedByteBuffer dst = current();
        
        int remainingSrc = src.remaining();
        int remainingDst = dst.remaining();
        
        if (remaining() < remainingSrc) {
            throw new BufferOverflowException();
        }
        
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
}
