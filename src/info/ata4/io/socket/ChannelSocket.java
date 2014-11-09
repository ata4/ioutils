/*
 ** 2014 February 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.ByteBufferReadable;
import info.ata4.io.ByteBufferWritable;
import info.ata4.io.Positionable;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ChannelSocket extends IOSocket {
    
    private final Channel channel;
    
    public ChannelSocket(Channel channel) {
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setStreaming(true);
        setProperties(props);
        
        if (channel instanceof ReadableByteChannel) {
            getReadableByteChannelProvider().set((ReadableByteChannel) channel);
            setByteBufferReadable(new ChannelByteBufferReadable((ReadableByteChannel) channel));
        }
        
        if (channel instanceof WritableByteChannel) {
            getWritableByteChannelProvider().set((WritableByteChannel) channel);
            setByteBufferWritable(new ChannelByteBufferWritable((WritableByteChannel) channel));
            props.setWritable(true);
            props.setGrowable(true);
        }
        
        if (channel instanceof SeekableByteChannel) {
            setPositionable(new ChannelSeekable((SeekableByteChannel) channel));
            props.setStreaming(false);
        }
        
        this.channel = channel;
    }

    @Override
    protected void close(Closeable c) throws IOException {
        super.close(c);
        channel.close();
    }
    
    private class ChannelSeekable implements Positionable {
        
        private final SeekableByteChannel channel;
        
        private ChannelSeekable(SeekableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public void position(long where) throws IOException {
            channel.position(where);
        }

        @Override
        public long position() throws IOException {
            return channel.position();
        }

        @Override
        public long size() throws IOException {
            return channel.size();
        }
        
        @Override
        public long remaining() throws IOException {
            return channel.size() - channel.position();
        }
        
        @Override
        public boolean hasRemaining() throws IOException {
            return remaining() > 0;
        }
    }
    
    private class ChannelByteBufferReadable implements ByteBufferReadable {
        
        private final ReadableByteChannel channel;
        
        private ChannelByteBufferReadable(ReadableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public void readBuffer(ByteBuffer dst) throws IOException {
            // read channel to buffer while the buffer isn't completely filled
            while (dst.hasRemaining()) {
                if (channel.read(dst) == -1) {
                    throw new IOException("Reached end-of-stream while filling the buffer");
                }
            }
        }
    }
    
    private class ChannelByteBufferWritable implements ByteBufferWritable {
        
        private final WritableByteChannel channel;
        
        private ChannelByteBufferWritable(WritableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public void writeBuffer(ByteBuffer src) throws IOException {
            // write buffer to channel while it's not completely emptied
            while (src.hasRemaining()) {
                channel.write(src);
            }
        }
    }
}
