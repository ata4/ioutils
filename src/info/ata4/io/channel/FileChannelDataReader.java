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
import java.nio.channels.FileChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FileChannelDataReader extends ByteChannelDataReader {
    
    private final BufferedReadablePositionableByteChannel rpchan;
    
    public FileChannelDataReader(FileChannel fc, int bufferSize) throws IOException {
        this(new BufferedReadablePositionableByteChannel(fc, bufferSize));
    }
    
    public FileChannelDataReader(FileChannel fc) throws IOException {
        this(new BufferedReadablePositionableByteChannel(fc));
    }
    
    private FileChannelDataReader(BufferedReadablePositionableByteChannel schan) throws IOException {
        super(schan);
        this.rpchan = schan;
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
