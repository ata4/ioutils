/*
 ** 2014 February 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import info.ata4.io.DataReader;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * DataInput adapter for a ByteBuffer.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferDataReader extends DataReader implements ByteBufferBacked {
    
    private final ByteBuffer buf;
    private final ByteBufferInputStream is;
    
    public ByteBufferDataReader(ByteBuffer buf) {
        this.buf = buf;
        this.is = new ByteBufferInputStream(buf);
    }
    
    @Override
    public ByteBuffer buffer() {
        return buf;
    }
    
    @Override
    public ByteBufferInputStream stream() {
        return is;
    }
    
    @Override
    public ByteOrder order() {
        return buf.order();
    }
    
    @Override
    public void order(ByteOrder order) {
        buf.order(order);
    }
    
    @Override
    public long position() throws IOException {
        return buf.position();
    }
    
    @Override
    public void position(long newPos) throws IOException {
        buf.position((int) newPos);
    }
    
    @Override
    public long size() throws IOException {
        return buf.limit();
    }

    @Override
    public void readBytes(byte[] b) throws IOException {
        try {
            buf.get(b);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void readBytes(byte[] b, int off, int len) throws IOException {
        try {
            buf.get(b, off, len);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }
    
    @Override
    public void readBuffer(ByteBuffer dst) throws IOException {
        try {
            while (dst.hasRemaining()) {
                dst.put(buf.get());
            }
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return buf.get() != 0;
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
    public short readShort() throws IOException {
        try {
            return buf.getShort();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
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
}
