/*
 ** 2015 March 01
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer.source;

import info.ata4.io.buffer.ByteBufferChannel;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteChannelSource implements BufferedSource {
    
    protected final ByteBuffer buf;
    private ReadableByteChannel chanIn;
    private WritableByteChannel chanOut;
    private ByteBufferChannel chanBuf;
    private boolean dirty;
    
    public ByteChannelSource(ByteBuffer buffer, ReadableByteChannel in) {
        this(buffer, in, null);
    }
    
    public ByteChannelSource(ByteBuffer buffer, WritableByteChannel out) {
        this(buffer, null, out);
    }
    
    protected ByteChannelSource(ByteBuffer buffer, ReadableByteChannel in, WritableByteChannel out) {
        if (in == null && out == null) {
            throw new IllegalArgumentException("No readable or writable channel available");
        }
        
        chanIn = in;
        chanOut = out;
        
        buf = buffer;
        buf.limit(0);
        
        chanBuf = new ByteBufferChannel(buf);
    }
    
    public void markDirty() {
        dirty = true;
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    @Override
    public void position(long newPos) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long position() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long size() throws IOException {
        throw new UnsupportedOperationException();
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
    public int bufferSize() {
        return buf.capacity();
    }
    
    @Override
    public boolean canRead() {
        return chanIn != null;
    }
    
    @Override
    public boolean canWrite() {
        return chanOut != null;
    }
    
    public void fill() throws IOException {
        // don't fill in write-only mode
        if (!canRead()) {
            return;
        }
        
        // copy remaining bytes to the beginning of the buffer
        buf.compact();
        
        // clear limit
        buf.limit(buf.capacity());

        // fill buffer from channel
        while (chanIn.read(buf) > 0);

        // start from the beginning
        buf.flip();
    }
    
    public void flush() throws IOException {
        // don't flush in read-only mode or if buffer is clean
        if (!canWrite() || !isDirty()) {
            return;
        }
        
        // stop here and start from the beginning
        buf.flip();
        
        // write buffer to channel
        while (chanOut.write(buf) > 0);
        
        // clear buffer
        buf.clear();
        
        dirty = false;
    }
    
    public void clear() {
        // reset position and marks
        buf.clear();
        
        // clear all bytes
        while (buf.hasRemaining()) {
            buf.put((byte) 0);
        }
        
        // mark it as empty
        buf.limit(0);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (!canRead()) {
            throw new NonReadableChannelException();
        }
        
        int n = chanBuf.read(dst);
        
        // check if buffer is empty
        if (n == -1) {
            flush();
            
            if (dst.remaining() > buf.capacity()) {
                // dst buffer larger than internal buffer, read directly
                n = chanIn.read(dst);
            } else {
                // fill buffer and then read
                fill();
                n = chanBuf.read(dst);
            }
        }
        return n;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!canWrite()) {
            throw new NonWritableChannelException();
        }
        
        int n = chanBuf.write(src);
        
        // check if buffer is empty
        if (n == -1) {
            flush();
            
            if (src.remaining() > buf.capacity()) {
                // src buffer larger than internal buffer, write directly
                n = chanOut.write(src);
            } else {
                // fill buffer and then write
                fill();
                n = chanBuf.write(src);
            }
        }
        
        markDirty();
        
        return n;
    }

    private ByteBuffer request(int required, boolean write) throws EOFException, IOException {
        // check if additional bytes need to be buffered
        if (buf.remaining() < required) {
            flush();
            fill();

            // if there are still not enough bytes available...
            if (buf.remaining() < required) {
                if (write) {
                    // lift limit at EOF to allow writing
                    buf.limit(buf.capacity());
                } else {
                    // throw exception
                    throw new EOFException();
                }
            }
        }
        
        if (write) {
            markDirty();
        }
        
        return buf;
    }
    
    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {
        return request(required, false);
    }

    @Override
    public ByteBuffer requestWrite(int required) throws EOFException, IOException {
        return request(required, true);
    }

    @Override
    public void close() throws IOException {
        flush();
        
        if (chanIn != null && chanIn.isOpen()) {
            chanIn.close();
        }
        
        if (chanOut != null && chanOut.isOpen()) {
            chanOut.close();
        }
    }
}
