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

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class SeekableByteChannelDataWriter extends ByteChannelDataWriter {
    
    private final BufferedWritablePositionableByteChannel wpchan;
    
    public SeekableByteChannelDataWriter(SeekableByteChannel chan, int bufferSize) throws IOException {
        this(new BufferedWritablePositionableByteChannel(chan, bufferSize));
    }
    
    public SeekableByteChannelDataWriter(SeekableByteChannel chan) throws IOException {
        this(new BufferedWritablePositionableByteChannel(chan));
    }
    
    private SeekableByteChannelDataWriter(BufferedWritablePositionableByteChannel wpchan) throws IOException {
        super(wpchan);
        this.wpchan = wpchan;
    }
    
    @Override
    public void position(long newPos) throws IOException {
        wpchan.position(newPos);
    }

    @Override
    public long position() throws IOException {
        return wpchan.position();
    }

    @Override
    public long size() throws IOException {
        return wpchan.size();
    }
}
