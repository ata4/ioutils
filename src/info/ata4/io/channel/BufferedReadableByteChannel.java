/*
 ** 2015 February 22
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channel;

import info.ata4.io.buffer.ByteBufferBacked;
import info.ata4.io.buffer.ByteBufferChannel;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedReadableByteChannel extends ChannelProxy<ReadableByteChannel> implements ReadableByteChannel, ByteBufferBacked {
    
    public static final int DEFAULT_BUFFER_SIZE = 1 << 20; // 1 MiB
    
    protected final ByteBuffer buf;
    private final ReadableByteChannel chanBuf;
    
    public BufferedReadableByteChannel(ReadableByteChannel in, int bufferSize) {
        super(in);
        
        buf = ByteBuffer.allocateDirect(bufferSize);
        buf.limit(0);
        
        chanBuf = new ByteBufferChannel(buf);
    }
    
    public BufferedReadableByteChannel(ReadableByteChannel in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }
    
    public int bufferSize() {
        return buf.capacity();
    }
    
    public int buffered() {
        return buf.remaining();
    }
    
    public int fill() throws IOException {
        // copy remaining bytes to the beginning of the buffer
        buf.compact();
        
        // fill buffer from channel
        while (readDirect(buf) > 0);

        // start from the beginning
        buf.flip();
        
        return buffered();
    }
    
    public ByteBuffer buffer(int required) throws IOException {
        // check if additional bytes need to be buffered and throw an exception
        // if not enough bytes are available
        if (buffered() < required && fill() < required) {
            throw new EOFException();
        }
        
        return buf;
    }
    
    @Override
    public ByteBuffer buffer() {
        return buf;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int n = chanBuf.read(dst);
        if (n == -1) {
            // buffer is empty
            if (dst.remaining() > buf.capacity()) {
                // dst buffer larger than internal buffer, read directly
                n = readDirect(dst);
            } else {
                // refill buffer and then read
                fill();
                n = chanBuf.read(dst);
            }
        }
        return n;
    }
    
    protected int readDirect(ByteBuffer dst) throws IOException {
        return chan.read(dst);
    }
}
