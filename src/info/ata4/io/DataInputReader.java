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
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import org.apache.commons.lang3.ArrayUtils;

/**
 * DataInput extension for more data access methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataInputReader extends DataInputWrapper implements DataInputExtended {
    
    private static final String DEFAULT_CHARSET = "ASCII";
    
    public static DataInputReader newReader(DataInput in) {
        return new DataInputReader(new DataSocket(in));
    }
    
    public static DataInputReader newReader(InputStream is) {
        return new DataInputReader(new StreamSocket(is));
    }
    
    public static DataInputReader newReader(ByteBuffer bb) {
        return new DataInputReader(new ByteBufferSocket(bb));
    }
    
    public static DataInputReader newReader(ReadableByteChannel fc) {
        return new DataInputReader(new ChannelSocket(fc));
    }
    
    public static DataInputReader newReader(FileChannel fc) throws IOException {
        return new DataInputReader(new FileChannelSocket(fc));
    }
 
    public static DataInputReader newReader(Path file, boolean seekable) throws IOException {
        if (seekable) {
            return newReader(FileChannel.open(file, READ));
        } else {
            return newReader(new BufferedInputStream(Files.newInputStream(file, READ), 4096));
        }
    }
    
    public static DataInputReader newReader(Path file) throws IOException {
        return newReader(file, false);
    }
    
    public static DataInputReader newReader(RandomAccessFile raf) throws IOException {
        return newReader(raf.getChannel());
    }

    public DataInputReader(IOSocket socket) {
        super(socket);
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }
    
    @Override
    public BigInteger readUnsignedLong() throws IOException {
        byte[] raw = new byte[8];
        readFully(raw);
        if (swap) {
            ArrayUtils.reverse(raw);
        }
        return new BigInteger(raw);
    }
    
    @Override
    public float readHalf() throws IOException {
        int hbits = readUnsignedShort();
        return HalfFloat.intBitsToFloat(hbits);
    }
    
    private String readStringInt(int limit, String charset, boolean padded) throws IOException {
        if (limit <= 0) {
            throw new IllegalArgumentException("Invalid limit");
        }
        
        // read raw byte array until the size is equal to limit or the byte is null
        byte[] raw = new byte[limit];
        int length = 0;
        for (byte b; length < raw.length && (b = readByte()) != 0; length++) {
            raw[length] = b;
        }
        
        // skip padding bytes
        if (padded) {
            skipBytes(limit - length - 1);
        }
        
        return new String(raw, 0, length, charset);
    }
    
    @Override
    public String readStringNull(int limit, String charset) throws IOException {
        return readStringInt(limit, charset, false);
    }
    
    @Override
    public String readStringNull(int limit) throws IOException {
        return readStringNull(limit, DEFAULT_CHARSET);
    }

    @Override
    public String readStringNull() throws IOException {
        return readStringNull(256);
    }
    
    @Override
    public String readStringPadded(int limit, String charset) throws IOException {
        return readStringInt(limit, charset, true);
    }

    @Override
    public String readStringPadded(int limit) throws IOException {
        return readStringPadded(limit, DEFAULT_CHARSET);
    }
    
    @Override
    public String readStringFixed(int length, String charset) throws IOException {
        byte[] raw = new byte[length];
        readFully(raw);
        return new String(raw, charset);
    }
    
    @Override
    public String readStringFixed(int length) throws IOException {
        return readStringFixed(length, DEFAULT_CHARSET);
    }
    
    @Override
    public String readStringInt(int limit, String charset) throws IOException {
        int length = readInt();
        if (limit > 0 && length > limit) {
            return null;
        }
        
        return readStringFixed(length, charset);
    }
    
    @Override
    public String readStringInt(int limit) throws IOException {
        return readStringInt(limit, DEFAULT_CHARSET);
    }
    
    @Override
    public String readStringInt() throws IOException {
        return readStringInt(0);
    }
    
    @Override
    public String readStringShort(int limit, String charset) throws IOException {
        int length = readUnsignedShort();
        if (limit > 0 && length > limit) {
            return null;
        }
        
        return readStringFixed(length, charset);
    }
    
    @Override
    public String readStringShort(int limit) throws IOException {
        return readStringShort(limit, DEFAULT_CHARSET);
    }

    @Override
    public String readStringShort() throws IOException {
        return readStringShort(0);
    }
    
    @Override
    public String readStringByte(String charset) throws IOException {
        int length = readUnsignedByte();
        return readStringFixed(length, charset);
    }
    
    @Override
    public String readStringByte() throws IOException {
        return readStringByte(DEFAULT_CHARSET);
    }
    
    @Override
    public void readBuffer(ByteBuffer dst) throws IOException {
        ByteBuffer buffer = getSocket().getByteBuffer();
        if (buffer != null) {
            dst.put(buffer);
        } else {
            ReadableByteChannel channel = getSocket().getReadableByteChannel();

            if (channel != null) {
                channel.read(dst);
            } else {
                throw new UnsupportedOperationException();
            }
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
