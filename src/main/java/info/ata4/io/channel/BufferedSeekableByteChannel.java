/*
 ** 2015 März 01
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channel;

import info.ata4.io.buffer.source.SeekableByteChannelSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedSeekableByteChannel extends ChannelProxy<SeekableByteChannel> implements SeekableByteChannel {
    
    private final SeekableByteChannelSource buf;

    public BufferedSeekableByteChannel(SeekableByteChannel chan, int bufSize) {
        super(chan);
        ByteBuffer bb = ByteBuffer.allocateDirect(bufSize);
        buf = new SeekableByteChannelSource(bb, chan);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        return buf.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return buf.write(src);
    }

    @Override
    public long position() throws IOException {
        return buf.position();
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        buf.position(newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        return buf.size();
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        buf.flush();
        buf.clear();
        chan.truncate(size);
        return this;
    }
}
