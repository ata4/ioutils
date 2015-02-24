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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedWritableByteChannel extends ChannelProxy<WritableByteChannel> implements WritableByteChannel, ByteBufferBacked {
    
    public static final int DEFAULT_BUFFER_SIZE = 1 << 20; // 1 MiB
    
    protected final ByteBuffer buf;
    private final WritableByteChannel chanBuf;

    public BufferedWritableByteChannel(WritableByteChannel chan, int bufferSize) {
        super(chan);
        
        buf = ByteBuffer.allocateDirect(bufferSize);
        
        chanBuf = new ByteBufferChannel(buf);
    }
    
    public BufferedWritableByteChannel(WritableByteChannel chan) {
        this(chan, DEFAULT_BUFFER_SIZE);
    }
    
    public ByteBuffer buffer(int required) throws IOException {
        if (buf.remaining() < required) {
            flush();
        }
        
        return buf;
    }
    
    public int bufferSize() {
        return buf.capacity();
    }
    
    public int pending() {
        return buf.position();
    }
    
    @Override
    public ByteBuffer buffer() {
        return buf;
    }
    
    public int flush() throws IOException {
        // if the buffer is empty, just initialize it
        if (buf.limit() == 0) {
            buf.clear();
            return 0;
        }
        
        int n = pending();
        
        // stop here and start from the beginning
        buf.flip();
        
        // write buffer to channel
        while (writeDirect(buf) > 0);

        // clear buffer
        buf.clear();
        
        return n;
    }
    
    @Override
    public int write(ByteBuffer src) throws IOException {
        int n = chanBuf.write(src);
        if (n == -1) {
            // buffer is full, flush it
            flush();
            
            if (src.remaining() > buf.capacity()) {
                // src buffer larger than internal buffer, write directly
                n = writeDirect(src);
            } else {
                n = chanBuf.write(src);
            }
        }
        return n;
    }
    
    protected int writeDirect(ByteBuffer src) throws IOException {
        return chan.write(src);
    }

    @Override
    public void close() throws IOException {
        flush();
        super.close();
    }
}
