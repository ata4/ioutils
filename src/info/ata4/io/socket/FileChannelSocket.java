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
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FileChannelSocket extends IOSocket {
    
    private final FileChannel fc;
    
    public FileChannelSocket(Path file, OpenOption... options) throws IOException {
        fc = FileChannel.open(file, options);
        
        Set<OpenOption> optionSet = new HashSet<>(Arrays.asList(options));
        setCanRead(optionSet.contains(StandardOpenOption.READ));
        setCanWrite(optionSet.contains(StandardOpenOption.WRITE));
        
        // FileChannel is unbuffered, so use buffering for streams on default
        setStreamBuffering(true);
    }
    
    public FileChannelSocket(FileChannel fc, boolean readable, boolean writable) {
        this.fc = fc;
        
        // FileChannel provides no interface to check for readable/writable,
        // so rely on these arguments instead
        setCanRead(readable);
        setCanWrite(writable);
        
        // FileChannel is unbuffered, so use buffering for streams on default
        setStreamBuffering(true);
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
        return new FileChannelSeekable(fc);
    }
    
    private class FileChannelSeekable extends SeekableImpl {
        
        private final FileChannel fc;
        
        private FileChannelSeekable(FileChannel fc) {
            this.fc = fc;
        }

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
