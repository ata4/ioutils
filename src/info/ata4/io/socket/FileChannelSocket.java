/*
 ** 2014 February 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.Seekable;
import info.ata4.io.SeekableImpl;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FileChannelSocket extends IOSocket {
    
    private final FileChannel fc;
    
    public FileChannelSocket(FileChannel fc) {
        this.fc = fc;
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() {
        return fc;
    }

    @Override
    public WritableByteChannel getWritableByteChannel() {
        return fc;
    }

    @Override
    protected Seekable newSeekable() {
        return new FileChannelSeekable();
    }
    
    private class FileChannelSeekable extends SeekableImpl {

        @Override
        public void position(long where) throws IOException {
            fc.position(where);
        }

        @Override
        public long position() throws IOException {
            return fc.position();
        }

        @Override
        public long capacity() throws IOException {
            return fc.size();
        }
    }
}
