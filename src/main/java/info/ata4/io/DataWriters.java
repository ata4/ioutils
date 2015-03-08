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
import info.ata4.io.buffer.source.SeekableByteChannelSource;
import info.ata4.io.buffer.source.WritableByteChannelSource;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataWriters {
    
    public static DataWriter forByteBuffer(ByteBuffer bb) {
        return new DataWriter(new ByteBufferSource(bb));
    }
    
    public static DataWriter forWritableByteChannel(WritableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 18);
        BufferedSource buf = new WritableByteChannelSource(bb, chan); 
        return new DataWriter(buf);
    }
    
    public static DataWriter forSeekableByteChannel(SeekableByteChannel chan) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(1 << 16);
        BufferedSource buf = new SeekableByteChannelSource(bb, chan); 
        return new DataWriter(buf);
    }
    
    public static DataWriter forOutputStream(OutputStream os) throws IOException {
        return forWritableByteChannel(Channels.newChannel(os));
    }
    
    public static DataWriter forFile(Path path, OpenOption... options) throws IOException {
        return forSeekableByteChannel(Files.newByteChannel(path, options));
    }
    
    private DataWriters() {
    }
}
