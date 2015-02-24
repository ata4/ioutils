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
public class FileChannelDataWriter extends ByteChannelDataWriter {
    
    private final BufferedWritablePositionableByteChannel wpchan;
    
    public FileChannelDataWriter(FileChannel fc, int bufferSize) throws IOException {
        this(new BufferedWritablePositionableByteChannel(fc, bufferSize));
    }
    
    public FileChannelDataWriter(FileChannel fc) throws IOException {
        this(new BufferedWritablePositionableByteChannel(fc));
    }
    
    private FileChannelDataWriter(BufferedWritablePositionableByteChannel schan) throws IOException {
        super(schan);
        this.wpchan = schan;
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
