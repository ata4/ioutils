/*
 ** 2014 November 08
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.buffer.ByteBufferSocket;
import info.ata4.io.data.DataSocket;
import info.ata4.io.file.mmap.MemoryMappedFile;
import info.ata4.io.file.mmap.MemoryMappedFileSocket;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class Sockets {
    
    private Sockets() {
    }
    
    public static IOSocket forDataInput(DataInput in) {
        return new DataSocket(in);
    }
    
    public static IOSocket forDataOutput(DataOutput out) {
        return new DataSocket(out);
    }
    
    public static IOSocket forInputStream(InputStream is) {
        return new StreamSocket(is);
    }
    
    public static IOSocket forOutputStream(OutputStream os) {
        return new StreamSocket(os);
    }
    
    public static IOSocket forByteBuffer(ByteBuffer bb) {
        return new ByteBufferSocket(bb);
    }
    
    public static IOSocket forReadableByteChannel(ReadableByteChannel fc) {
        return new ChannelSocket(fc);
    }
    
    public static IOSocket forWritableByteChannel(WritableByteChannel fc) {
        return new ChannelSocket(fc);
    }
 
    public static IOSocket forFile(Path file, OpenOption... options) throws IOException {
        return new FileChannelSocket(file, options);
    }
    
    public static IOSocket forBufferedReadFile(Path file) throws IOException {
        InputStream is = Files.newInputStream(file, READ);
        return forInputStream(new BufferedInputStream(is, 1 << 16));
    }
    
    public static IOSocket forBufferedWriteFile(Path file) throws IOException {
        OutputStream os = Files.newOutputStream(file, WRITE);
        return forOutputStream(new BufferedOutputStream(os, 1 << 16));
    }
    
    public static IOSocket forMemoryMappedFile(Path file, OpenOption... options) throws IOException {
        if (Files.size(file) < Integer.MAX_VALUE) {
            Set<OpenOption> optionsSet = new HashSet<>(Arrays.asList(options));
            try (FileChannel fc = FileChannel.open(file, options)) {
                return forByteBuffer(fc.map(optionsSet.contains(WRITE) ? READ_WRITE : READ_ONLY, 0, (int) fc.size()));
            }
        } else {
            return new MemoryMappedFileSocket(new MemoryMappedFile(file, options));
        }
    }
    
    public static IOSocket forMemoryMappedFile(Path file, long size) throws IOException {
        if (Files.size(file) < Integer.MAX_VALUE) {
            try (FileChannel fc = FileChannel.open(file, CREATE, READ, WRITE)) {
                fc.truncate(size);
                return forByteBuffer(fc.map(READ_WRITE, 0, size));
            }
        } else {
            return new MemoryMappedFileSocket(new MemoryMappedFile(file, size, CREATE, READ, WRITE));
        }
    }
}
