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

import info.ata4.io.buffer.source.BufferedSource;
import info.ata4.io.buffer.source.BufferedSourceChannel;
import info.ata4.io.util.HalfFloat;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataReader extends DataBridge implements DataInput, StringInput {
    
    public DataReader(BufferedSource buf) {
        super(buf);
    }
    
    public void readStruct(Struct struct) throws IOException {
        struct.read(this);
    }
    
    public InputStream stream() {
        return Channels.newInputStream(new BufferedSourceChannel(buf));
    }

    ///////////////
    // DataInput //
    ///////////////
    
    @Override
    public void readBytes(byte[] b, int off, int len) throws IOException {
        buf.requestRead(len).get(b, off, len);
    }
    
    @Override
    public void readBuffer(ByteBuffer dst) throws IOException {
        while (dst.hasRemaining() && buf.read(dst) > 0);
        if (dst.hasRemaining()) {
            throw new EOFException();
        }
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        return buf.requestRead(1).get() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return buf.requestRead(1).get();
    }

    @Override
    public short readShort() throws IOException {
        return buf.requestRead(2).getShort();
    }

    @Override
    public char readChar() throws IOException {
        return buf.requestRead(2).getChar();
    }

    @Override
    public int readInt() throws IOException {
        return buf.requestRead(4).getInt();
    }

    @Override
    public long readLong() throws IOException {
        return buf.requestRead(8).getLong();
    }

    @Override
    public float readFloat() throws IOException {
        return buf.requestRead(4).getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return buf.requestRead(8).getDouble();
    }
    
    @Override
    public void readBytes(byte[] b) throws IOException {
        readBytes(b, 0, b.length);
    }
    
    @Override
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }
    
    @Override
    public BigInteger readUnsignedLong() throws IOException {
        byte[] raw = new byte[8];
        readBytes(raw);
        if (order() == ByteOrder.LITTLE_ENDIAN) {
            ArrayUtils.reverse(raw);
        }
        return new BigInteger(raw);
    }
    
    @Override
    public float readHalf() throws IOException {
        int hbits = readUnsignedShort();
        return HalfFloat.intBitsToFloat(hbits);
    }
    
    /////////////////
    // StringInput //
    /////////////////
    
    @Override
    public String readStringFixed(int length, Charset charset) throws IOException {
        byte[] raw = new byte[length];
        readBytes(raw);
        
        for (length = 0; length < raw.length && raw[length] != 0; length++);
        
        if (length == 0) {
            return "";
        } else {
            return new String(raw, 0, length, charset);
        }
    }
    
    @Override
    public String readStringFixed(int length) throws IOException {
        return readStringFixed(length, StandardCharsets.US_ASCII);
    }

    @Override
    public String readStringNull(int limit, Charset charset) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(limit);
        
        byte b;
        while ((b = readByte()) != 0) {
            if (bb.hasRemaining()) {
                bb.put(b);
            }
        }
        
        bb.flip();
        
        if (!bb.hasRemaining()) {
            return "";
        } else {
            return new String(bb.array(), 0, bb.limit(), charset);
        }
    }
    
    @Override
    public String readStringNull(int limit) throws IOException {
        return readStringNull(limit, StandardCharsets.US_ASCII);
    }
    
    @Override
    public String readStringNull() throws IOException {
        return readStringNull(256);
    }
    
    @Override
    public <T extends Number> String readStringPrefixed(Class<T> prefixType, T limit, Charset charset) throws IOException {
        Number length;
        if (prefixType == Byte.TYPE) {
            length = readUnsignedByte();
        } else if (prefixType == Short.TYPE) {
            length = readUnsignedShort();
        } else if (prefixType == Integer.TYPE) {
            length = readUnsignedInt();
        } else {
            throw new IllegalArgumentException("Wrong prefix data type");
        }
        
        final int len = length.intValue();
        if (len == 0) {
            return "";
        } else {
            byte[] raw = new byte[len];
            readBytes(raw);
            return new String(raw, 0, len, charset);
        }
    }
    
    @Override
    public <T extends Number> String readStringPrefixed(Class<T> prefixType, T limit) throws IOException {
        return readStringPrefixed(prefixType, limit, StandardCharsets.US_ASCII);
    }
}
