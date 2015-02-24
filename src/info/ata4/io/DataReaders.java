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

import info.ata4.io.buffer.ByteBufferDataReader;
import info.ata4.io.channel.ByteChannelDataReader;
import info.ata4.io.channel.SeekableByteChannelDataReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataReaders {
    
    public static ByteBufferDataReader forByteBuffer(ByteBuffer bb) {
        return new ByteBufferDataReader(bb);
    }
    
    public static ByteChannelDataReader forByteChannel(ReadableByteChannel chan) throws IOException {
        return new ByteChannelDataReader(chan);
    }
    
    public static ByteChannelDataReader forInputStream(InputStream is) throws IOException {
        return new ByteChannelDataReader(Channels.newChannel(is));
    }
    
    public static SeekableByteChannelDataReader forFile(Path path, OpenOption... options) throws IOException {
        return new SeekableByteChannelDataReader(Files.newByteChannel(path, options));
    }
    
    private DataReaders() {
    }
}
