/*
 ** 2015 January 11
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import info.ata4.io.Swappable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

/**
 * SeekableByteChannel adapter for a ByteBuffer. Useful for buffered channels.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferChannel implements SeekableByteChannel, Swappable {
    
    private final ByteBuffer buf;
    private boolean closed;
    
    public ByteBufferChannel(ByteBuffer bb) {
        this.buf = bb;
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
    public synchronized int read(ByteBuffer dst) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        
        if (!buf.hasRemaining()) {
            return -1;
        }
        
        return ByteBufferUtils.transfer(buf, dst);
    }

    @Override
    public synchronized int write(ByteBuffer src) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        
        if (buf.isReadOnly()) {
            throw new NonWritableChannelException();
        }
        
        if (!buf.hasRemaining()) {
            return -1;
        }
        
        return ByteBufferUtils.transfer(src, buf);
    }

    @Override
    public boolean isOpen() {
        return closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }

    @Override
    public long position() throws IOException {
        return buf.position();
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        if (newPosition > buf.limit()) {
            throw new IllegalArgumentException();
        }
        buf.position((int) newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return buf.limit();
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        if (size < buf.limit()) {
            buf.limit((int) size);
        }
        return this;
    }
}
