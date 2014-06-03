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

import info.ata4.io.socket.ByteBufferSocket;
import info.ata4.io.socket.ChannelSocket;
import info.ata4.io.socket.DataSocket;
import info.ata4.io.socket.FileChannelSocket;
import info.ata4.io.socket.IOSocket;
import info.ata4.io.socket.StreamSocket;
import info.ata4.io.util.HalfFloat;
import java.io.BufferedOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * DataOutput extension for more data access methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataOutputWriter extends DataOutputWrapper implements DataOutputExtended {
    
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
    
    public static DataOutputWriter newWriter(FileChannel fc) throws IOException {
        return new DataOutputWriter(new FileChannelSocket(fc));
    }
    
    public static DataOutputWriter newWriter(Path file, boolean seekable) throws IOException {
        if (seekable) {
            return newWriter(FileChannel.open(file, CREATE, WRITE));
        } else {
            return newWriter(new BufferedOutputStream(Files.newOutputStream(file, CREATE, WRITE), 4096));
        }
    }
    
    public static DataOutputWriter newWriter(Path file) throws IOException {
        return newWriter(file, false);
    }
    
    public static DataOutputWriter newWriter(RandomAccessFile raf) throws IOException {
        return newWriter(raf.getChannel());
    }

    public DataOutputWriter(IOSocket socket) {
        super(socket);
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
        skipBytes(nullBytes);
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
        ByteBuffer buffer = getSocket().getByteBuffer();
        if (buffer != null) {
            buffer.put(src);
            return;
        }
        
        WritableByteChannel channel = getSocket().getWritableByteChannel();
        if (channel != null) {
            // write buffer to channel while it's not completely emptied
            while (src.hasRemaining()) {
                channel.write(src);
            }
            return;
        }
        
        // no channel or byte buffer available
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void skipBytes(int n) throws IOException {
        for (int i = 0; i < n; i++) {
            writeByte(0);
        }
    }

    @Override
    public void align(int length, int align) throws IOException {
        if (align > 0) {
            int rem = length % align;
            if (rem != 0) {
                skipBytes(align - rem);
            }
        }
    }
}
