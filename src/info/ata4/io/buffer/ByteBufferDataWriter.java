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

import info.ata4.io.DataWriter;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * DataOutput adapter for a ByteBuffer.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferDataWriter extends DataWriter implements ByteBufferBacked {
    
    private final ByteBuffer buf;
    private final ByteBufferOutputStream os;
    
    public ByteBufferDataWriter(ByteBuffer buf) {
        if (buf.isReadOnly()) {
            throw new IllegalArgumentException("Buffer is read-only");
        }
        this.buf = buf;
        this.os = new ByteBufferOutputStream(buf);
    }
    
    @Override
    public ByteBuffer buffer() {
        return buf;
    }
    
    @Override
    public ByteBufferOutputStream stream() {
        return os;
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
    public void writeBytes(byte[] b) throws IOException {
        try {
            buf.put(b);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        try {
            buf.put(b, off, len);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }
    
    @Override
    public void writeBuffer(ByteBuffer dst) throws IOException {
        try {
            while (dst.hasRemaining()) {
                dst.put(buf.get());
            }
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        try {
            buf.put((byte) (v ? 1 : 0));
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeByte(byte v) throws IOException {
        try {
            buf.put(v);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeShort(short v) throws IOException {
        try {
            buf.putShort(v);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void writeChar(char v) throws IOException {
        try {
            buf.putChar(v);
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
}
