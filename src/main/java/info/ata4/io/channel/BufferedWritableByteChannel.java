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

import info.ata4.io.buffer.source.ByteChannelSource;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedWritableByteChannel extends ChannelProxy<WritableByteChannel> implements WritableByteChannel {
    
    public static final int DEFAULT_BUFFER_SIZE = 1 << 20; // 1 MiB
    
    private final ByteChannelSource buf;

    public BufferedWritableByteChannel(WritableByteChannel out, int bufferSize) {
        super(out);
        ByteBuffer bb = ByteBuffer.allocateDirect(bufferSize);
        buf = new ByteChannelSource(bb, out);
    }
    
    public BufferedWritableByteChannel(WritableByteChannel chan) {
        this(chan, DEFAULT_BUFFER_SIZE);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return buf.write(src);
    }
}
