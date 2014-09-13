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

import info.ata4.io.Positionable;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ChannelSocket extends IOSocket {
    
    public ChannelSocket(Channel channel) {
        if (channel instanceof ReadableByteChannel) {
            getReadableByteChannelProvider().set((ReadableByteChannel) channel);
            setCanRead(true);
        }
        
        if (channel instanceof WritableByteChannel) {
            getWritableByteChannelProvider().set((WritableByteChannel) channel);
            setCanWrite(true);
        }
        
        if (channel instanceof SeekableByteChannel) {
            setPositionable(new ChannelSeekable((SeekableByteChannel) channel));
        }
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
}
