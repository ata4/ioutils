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

import info.ata4.io.data.DataInputExtended;
import info.ata4.io.file.mmap.MemoryMappedFile;
import info.ata4.io.file.mmap.MemoryMappedFileSocket;
import info.ata4.io.buffer.ByteBufferSocket;
import info.ata4.io.socket.ChannelSocket;
import info.ata4.io.data.DataSocket;
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
import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.READ;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * DataInput extension for more data access methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataInputReader extends IOBridge implements DataInputExtended, ByteBufferReadable {
    
    // Charset.defaultCharset() is platform dependent and should not be used.
    // This includes the omitted charset parameter from the String constructor.
    private static final Charset DEFAULT_CHARSET = Charset.forName("ASCII");

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
        return new DataInputReader(new FileChannelSocket(fc, READ));
    }
 
    public static DataInputReader newReader(Path file) throws IOException {
        return new DataInputReader(new FileChannelSocket(file, READ));
    }
    
    public static DataInputReader newBufferedReader(Path file) throws IOException {
        InputStream is = Files.newInputStream(file, READ);
        return newReader(new BufferedInputStream(is, 1 << 16));
    }
    
    public static DataInputReader newMappedReader(Path file) throws IOException {
        if (Files.size(file) < Integer.MAX_VALUE) {
         try (FileChannel fc = FileChannel.open(file, READ)) {
                return newReader(fc.map(READ_ONLY, 0, fc.size()));
            }
        } else {
            return new DataInputReader(new MemoryMappedFileSocket(new MemoryMappedFile(file, READ)));
        }
    }

    public static DataInputReader newReader(RandomAccessFile raf) throws IOException {
        return newReader(raf.getChannel());
    }
    
    private final DataInput in;

    public DataInputReader(IOSocket socket) {
        super(socket);
        in = socket.getDataInput();
    }
    
    @Override
    public void readFully(byte[] b) throws IOException {
        in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return in.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short r = in.readShort();
        if (isManualSwap()) {
            r = EndianUtils.swapShort(r);
        }
        return r;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int r = in.readUnsignedShort();
        if (isManualSwap()) {
            r = EndianUtils.swapShort((short) r) & 0xffff;
        }
        return r;
    }

    @Override
    public char readChar() throws IOException {
        return in.readChar();
    }

    @Override
    public int readInt() throws IOException {
        int r = in.readInt();
        if (isManualSwap()) {
            r = EndianUtils.swapInteger(r);
        }
        return r;
    }

    @Override
    public long readLong() throws IOException {
        long r = in.readLong();
        if (isManualSwap()) {
            r = EndianUtils.swapLong(r);
        }
        return r;
    }

    @Override
    public float readFloat() throws IOException {
        if (isManualSwap()) {
            // NOTE: don't use readFloat() plus EndianUtils.swapFloat() here!
            return Float.intBitsToFloat(readInt());
        } else {
            return in.readFloat();
        }
    }

    @Override
    public double readDouble() throws IOException {
        if (isManualSwap()) {
            // NOTE: don't use readDouble() plus EndianUtils.swapDouble() here!
            return Double.longBitsToDouble(readLong());
        } else {
            return in.readDouble();
        }
    }

    @Override
    public String readLine() throws IOException {
        return in.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return in.readUTF();
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }
    
    @Override
    public BigInteger readUnsignedLong() throws IOException {
        byte[] raw = new byte[8];
        readFully(raw);
        if (!isSwap()) {
            ArrayUtils.reverse(raw);
        }
        return new BigInteger(raw);
    }
    
    @Override
    public float readHalf() throws IOException {
        int hbits = readUnsignedShort();
        return HalfFloat.intBitsToFloat(hbits);
    }
    
    private String readString(int limit, Charset charset, boolean padded) throws IOException {
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
    public String readStringNull(int limit, Charset charset) throws IOException {
        return readString(limit, charset, false);
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
    public String readStringPadded(int limit, Charset charset) throws IOException {
        return readString(limit, charset, true);
    }

    @Override
    public String readStringPadded(int limit) throws IOException {
        return readStringPadded(limit, DEFAULT_CHARSET);
    }
    
    @Override
    public String readStringFixed(int length, Charset charset) throws IOException {
        byte[] raw = new byte[length];
        readFully(raw);
        return new String(raw, charset);
    }
    
    @Override
    public String readStringFixed(int length) throws IOException {
        return readStringFixed(length, DEFAULT_CHARSET);
    }
    
    @Override
    public String readStringInt(int limit, Charset charset) throws IOException {
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
    public String readStringShort(int limit, Charset charset) throws IOException {
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
    public String readStringByte(Charset charset) throws IOException {
        int length = readUnsignedByte();
        return readStringFixed(length, charset);
    }
    
    @Override
    public String readStringByte() throws IOException {
        return readStringByte(DEFAULT_CHARSET);
    }
    
    @Override
    public void readBuffer(ByteBuffer dst) throws IOException {
        ByteBufferReadable readable = getSocket().getByteBufferReadable();
        if (readable != null) {
            readable.readBuffer(dst);
        } else {
            while (dst.hasRemaining()) {
                dst.put(readByte());
            }
        }
    }
    
    @Override
    public void readStruct(Struct struct) throws IOException {
        struct.read(this);
    }
    
    @Override
    public void align(int align) throws IOException {
        if (align > 0) {
            int rem = (int) (position() % align);
            if (rem != 0) {
                skipBytes(align - rem);
            }
        }
    }
}
