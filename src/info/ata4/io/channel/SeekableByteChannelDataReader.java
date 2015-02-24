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
public class SeekableByteChannelDataReader extends ByteChannelDataReader {
    
    private final BufferedReadablePositionableByteChannel rpchan;
    
    public SeekableByteChannelDataReader(SeekableByteChannel chan, int bufferSize) throws IOException {
        this(new BufferedReadablePositionableByteChannel(chan, bufferSize));
    }
    
    public SeekableByteChannelDataReader(SeekableByteChannel chan) throws IOException {
        this(new BufferedReadablePositionableByteChannel(chan));
    }
    
    private SeekableByteChannelDataReader(BufferedReadablePositionableByteChannel rpchan) throws IOException {
        super(rpchan);
        this.rpchan = rpchan;
    }
    
    @Override
    public void position(long newPos) throws IOException {
        rpchan.position(newPos);
    }

    @Override
    public long position() throws IOException {
        return rpchan.position();
    }

    @Override
    public long size() throws IOException {
        return rpchan.size();
    }
}
