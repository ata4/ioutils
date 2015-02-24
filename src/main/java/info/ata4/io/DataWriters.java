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

import info.ata4.io.buffer.ByteBufferDataWriter;
import info.ata4.io.channel.ByteChannelDataWriter;
import info.ata4.io.channel.SeekableByteChannelDataWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataWriters {
    
    public static ByteBufferDataWriter forByteBuffer(ByteBuffer bb) {
        return new ByteBufferDataWriter(bb);
    }
    
    public static ByteChannelDataWriter forByteChannel(WritableByteChannel chan) throws IOException {
        return new ByteChannelDataWriter(chan);
    }
    
    public static ByteChannelDataWriter forOutputStream(OutputStream os) throws IOException {
        return new ByteChannelDataWriter(Channels.newChannel(os));
    }
    
    public static SeekableByteChannelDataWriter forFile(Path path, OpenOption... options) throws IOException {
        return new SeekableByteChannelDataWriter(Files.newByteChannel(path, options));
    }
    
    private DataWriters() {
    }
}
