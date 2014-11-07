/*
 ** 2013 June 15
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import info.ata4.io.data.DataOutputExtended;
import info.ata4.io.file.mmap.MemoryMappedFile;
import info.ata4.io.file.mmap.MemoryMappedFileSocket;
import info.ata4.io.buffer.ByteBufferSocket;
import info.ata4.io.socket.ChannelSocket;
import info.ata4.io.data.DataSocket;
import info.ata4.io.socket.FileChannelSocket;
import info.ata4.io.socket.IOSocket;
import info.ata4.io.socket.StreamSocket;
import info.ata4.io.util.HalfFloat;
import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;

/**
 * DataOutput extension for more data access methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataOutputWriter extends DataOutputBridge implements DataOutputExtended, ByteBufferWritable {
    
    // Charset.defaultCharset() is platform dependent and should not be used.
    // This includes the omitted charset parameter for String.getBytes().
    private static final Charset DEFAULT_CHARSET = Charset.forName("ASCII");
    
    public static DataOutputWriter newWriter(DataOutput out) {
        return new DataOutputWriter(new DataSocket(out));
    }
    
    public static DataOutputWriter newWriter(OutputStream os) {
        return new DataOutputWriter(new StreamSocket(os));
    }
    
    public static DataOutputWriter newWriter(ByteBuffer bb) {
        return new DataOutputWriter(new ByteBufferSocket(bb));
    }
    
    public static DataOutputWriter newWriter(WritableByteChannel fc) {
        return new DataOutputWriter(new ChannelSocket(fc));
    }
    
    public static DataOutputWriter newWriter(Path file, OpenOption... options) throws IOException {
        return new DataOutputWriter(new FileChannelSocket(file, options));
    }
    
    public static DataOutputWriter newBufferedWriter(Path file, OpenOption... options) throws IOException {
        OutputStream os = Files.newOutputStream(file, options);
        return new DataOutputWriter(new StreamSocket(new BufferedOutputStream(os, 1 << 16)));
    }
    
    public static DataOutputWriter newMappedWriter(Path file) throws IOException {
        if (Files.size(file) < Integer.MAX_VALUE) {
            try (FileChannel fc = FileChannel.open(file, WRITE)) {
                return newWriter(fc.map(READ_WRITE, 0, (int) fc.size()));
            }
        } else {
            return new DataOutputWriter(new MemoryMappedFileSocket(new MemoryMappedFile(file, WRITE)));
        }
    }
    
    public static DataOutputWriter newMappedWriter(Path file, long size) throws IOException {
        if (Files.size(file) < Integer.MAX_VALUE) {
            try (FileChannel fc = FileChannel.open(file, CREATE, WRITE)) {
                fc.truncate(size);
                return newWriter(fc.map(READ_WRITE, 0, size));
            }
        } else {
            return new DataOutputWriter(new MemoryMappedFileSocket(new MemoryMappedFile(file, size, CREATE, WRITE)));
        }
    }
    
    public static DataOutputWriter newWriter(RandomAccessFile raf) throws IOException {
        return newWriter(raf.getChannel());
    }

    public DataOutputWriter(IOSocket socket) {
        super(socket);
    }
    
    @Override
    public void writeUnsignedByte(int v) throws IOException {
        writeByte(v & 0xff);
    }

    @Override
    public void writeUnsignedShort(int v) throws IOException {
        writeShort(v & 0xffff);
    }

    @Override
    public void writeUnsignedInt(long v) throws IOException {
        writeInt((int) (v & 0xffffffff));
    }

    @Override
    public void writeUnsignedLong(BigInteger v) throws IOException {
        write(v.toByteArray());
    }
    
    @Override
    public void writeHalf(float f) throws IOException {
        int sval = HalfFloat.floatToIntBits(f);
        writeShort(sval);
    }

    @Override
    public void writeStringNull(String str, Charset charset) throws IOException {
        writeStringFixed(str, charset);
        writeByte(0);
    }
    
    @Override
    public void writeStringNull(String str) throws IOException {
        writeStringNull(str, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeStringPadded(String str, int padding, Charset charset) throws IOException {
        int nullBytes = padding - str.length();
        if (nullBytes < 0) {
            throw new IllegalArgumentException("Invalid padding");
        }
        
        writeStringFixed(str, charset);
        fill(nullBytes, (byte) 0);
    }

    @Override
    public void writeStringPadded(String str, int padding) throws IOException {
        writeStringPadded(str, padding, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeStringFixed(String str, Charset charset) throws IOException {
        write(str.getBytes(charset));
    }
    
    @Override
    public void writeStringFixed(String str) throws IOException {
        writeStringFixed(str, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeStringInt(String str, Charset charset) throws IOException {
        writeInt(str.length());
        writeStringFixed(str, charset);
    }

    @Override
    public void writeStringInt(String str) throws IOException {
        writeStringInt(str, DEFAULT_CHARSET);
    }

    @Override
    public void writeStringShort(String str, Charset charset) throws IOException {
        if (str.length() > 0xffff) {
            throw new IllegalArgumentException("String is too long");
        }
        
        writeShort(str.length());
        writeStringFixed(str, charset);
    }

    @Override
    public void writeStringShort(String str) throws IOException {
        writeStringShort(str, DEFAULT_CHARSET);
    }

    @Override
    public void writeStringByte(String str, Charset charset) throws IOException {
        if (str.length() > 0xff) {
            throw new IllegalArgumentException("String is too long");
        }
        
        writeByte(str.length());
        writeStringFixed(str, charset);
    }

    @Override
    public void writeStringByte(String str) throws IOException {
        writeStringByte(str, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeBuffer(ByteBuffer src) throws IOException {
        ByteBufferWritable writable = getSocket().getByteBufferWritable();
        if (writable != null) {
            writable.writeBuffer(src);
        } else {
            while (src.hasRemaining()) {
                writeByte(src.get());
            }
        }
    }
    
    @Override
    public void writeStruct(Struct struct) throws IOException {
        struct.write(this);
    }
    
    @Override
    public void fill(int n, byte b) throws IOException {
        for (int i = 0; i < n; i++) {
            writeByte(b);
        }
    }

    @Override
    public void align(int align) throws IOException {
        if (align > 0) {
            int rem = (int) (position() % align);
            if (rem != 0) {
                fill(align - rem, (byte) 0);
            }
        }
    }
}
