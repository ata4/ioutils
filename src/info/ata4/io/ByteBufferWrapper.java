/*
 ** 2013 June 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wrapper for ByteBuffers to implement various IO interfaces.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferWrapper implements Swappable, Seekable, DataInput, DataOutput {
    
    private final ByteBuffer buf;

    public ByteBufferWrapper(ByteBuffer buf) {
        this.buf = buf;
    }
    
    public ByteBuffer getByteBuffer() {
        return buf;
    }
    
    public InputStream getInputStream() {
        return new ByteBufferInputStream(buf);
    }
    
    public OutputStream getOutputStream() {
        return new ByteBufferOutputStream(buf);
    }
    
    @Override
    public boolean isSwap() {
        return buf.order() != ByteOrder.BIG_ENDIAN;
    }

    @Override
    public void setSwap(boolean swap) {
        buf.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
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
    public void position(long pos) throws IOException {
        if (pos > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Pointer is too large (> " + Integer.MAX_VALUE + ")");
        }
        buf.position((int) pos);
    }

    @Override
    public long position() throws IOException {
        return buf.position();
    }

    @Override
    public long capacity() throws IOException {
        return buf.capacity();
    }

    @Override
    public boolean hasRemaining() throws IOException {
        return buf.hasRemaining();
    }

    @Override
    public long remaining() throws IOException {
        return buf.remaining();
    }
    
    @Override
    public void readFully(byte[] b) throws IOException {
        try {
            buf.get(b);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        try {
            buf.get(b, off, len);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        try {
            n = Math.min(n, buf.remaining());
            buf.position(buf.position() + n);
            return n;
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return buf.get() == 1;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return buf.get();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }

    @Override
    public short readShort() throws IOException {
        try {
            return buf.getShort();
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
            return buf.getChar();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return buf.getInt();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return buf.getLong();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public float readFloat() throws IOException {
        try {
            return buf.getFloat();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            return buf.getDouble();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public String readLine() throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            for (byte c = 0; buf.hasRemaining() && c != '\n'; c = readByte()) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public String readUTF() throws IOException {
        try {
            return DataInputStream.readUTF(this);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }
    
    @Override
    public void write(int b) throws IOException {
        writeByte(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            buf.put(b);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            buf.put(b, off, len);
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
            buf.put((byte) (v & 0xff));
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeShort(int v) throws IOException {
        try {
            buf.putShort((short) (v & 0xffff));
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        try {
            buf.putChar((char) (v & 0xff));
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeInt(int v) throws IOException {
        try {
            buf.putInt(v);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        try {
            buf.putLong(v);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeFloat(float v) throws IOException {
        try {
            buf.putFloat(v);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        try {
            buf.putDouble(v);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
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
