/*
 ** 2015 February 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import info.ata4.io.buffer.source.BufferedSource;
import info.ata4.io.buffer.source.ByteBufferSource;
import info.ata4.io.buffer.source.ByteChannelSource;
import info.ata4.io.buffer.source.SeekableByteChannelSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataReaders {
    
    public static DataReader forByteBuffer(ByteBuffer bb) {
        return new DataReader(new ByteBufferSource(bb));
    }
    
    public static DataReader forReadableByteChannel(ReadableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 18);
        BufferedSource buf = new ByteChannelSource(bb, chan); 
        return new DataReader(buf);
    }
    
    public static DataReader forSeekableByteChannel(SeekableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 16);
        BufferedSource buf = new SeekableByteChannelSource(bb, chan); 
        return new DataReader(buf);
    }
    
    public static DataReader forInputStream(InputStream is) throws IOException {
        return forReadableByteChannel(Channels.newChannel(is));
    }
    
    public static DataReader forFile(Path path, OpenOption... options) throws IOException {
        return forSeekableByteChannel(Files.newByteChannel(path, options));
    }
    
    private DataReaders() {
    }
}
