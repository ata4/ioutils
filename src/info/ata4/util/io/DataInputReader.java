/*
 ** 2013 June 15
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import org.apache.commons.io.EndianUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * DataInput extension for more data access methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataInputReader extends DataInputWrapper {
    
    private static final String DEFAULT_CHARSET = "ASCII";
    private boolean swap;
    
    public DataInputReader(DataInput in) {
        super(in);
    }
    
    public DataInputReader(InputStream is) {
        super(new DataInputStream(is));
    }
    
    public DataInputReader(ByteBuffer bb) {
        super(new ByteBufferInput(bb));
    }
    
    public boolean isSwap() {
        return swap;
    }

    public void setSwap(boolean swap) {
        this.swap = swap;
    }
    
    @Override
    public short readShort() throws IOException {
        short r = super.readShort();
        if (swap) {
            r = EndianUtils.swapShort(r);
        }
        return r;
    }
    
    @Override
    public int readUnsignedShort() throws IOException {
        int r = super.readUnsignedShort();
        if (swap) {
            r = EndianUtils.swapInteger(r);
        }
        return r;
    }

    @Override
    public int readInt() throws IOException {
        int r = super.readInt();
        if (swap) {
            r = EndianUtils.swapInteger(r);
        }
        return r;
    }
    
    @Override
    public long readLong() throws IOException {
        long r = super.readLong();
        if (swap) {
            r = EndianUtils.swapLong(r);
        }
        return r;
    }
    
    @Override
    public float readFloat() throws IOException {
        float r = super.readFloat();
        if (swap) {
            r = EndianUtils.swapFloat(r);
        }
        return r;
    }
    
    @Override
    public double readDouble() throws IOException {
        double r = super.readDouble();
        if (swap) {
            r = EndianUtils.swapDouble(r);
        }
        return r;
    }

    /*
     * Reads an unsigned integer value and returns the value as long.
     */
    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }
    
    /*
     * Reads an unsigned long value and returns the value as BigInteger.
     */
    public BigInteger readUnsignedLong() throws IOException {
        byte[] raw = new byte[8];
        readFully(raw);
        if (swap) {
            ArrayUtils.reverse(raw);
        }
        return new BigInteger(raw);
    }
    
    /**
     * Reads a half-precision 16 bit floating-point number according to
     * IEEE 754-2008. The result is returned as normal 32 bit float.
     * 
     * @return half-precision float as full-precision float
     * @throws IOException 
     */
    public float readHalf() throws IOException {
        int hbits = readUnsignedShort();
        return HalfFloat.intBitsToFloat(hbits);
    }
    
    /**
     * Reads a null-terminated string.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @param charset character set to use when converting the bytes to string
     * @param padded if set to true, always read "limit" bytes and skip anything
     *               after the null char. Otherwise, stop reading after the null
     *               char.
     * @return string
     * @throws IOException 
     */
    public String readStringNull(int limit, String charset, boolean padded) throws IOException {
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
    
    /**
     * Reads a null-terminated string without byte padding.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringNull(int limit, String charset) throws IOException {
        return readStringNull(limit, charset, false);
    }
    
    /**
     * Reads a null-terminated string without byte padding, using the ASCII charset.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringNull(int limit) throws IOException {
        return readStringNull(limit, DEFAULT_CHARSET);
    }
    
    /**
     * Reads a null-terminated string without byte padding, using the ASCII
     * charset and with a limit of 256 bytes.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringNull() throws IOException {
        return readStringNull(256);
    }
    
    /**
     * Reads a fixed size string.
     * 
     * @param length total length of the string
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringFixed(int length, String charset) throws IOException {
        byte[] raw = new byte[length];
        readFully(raw);
        return new String(raw, charset);
    }
    
    /**
     * Reads a fixed size string using the ASCII charset.
     * 
     * @param length total length of the string
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringFixed(int length) throws IOException {
        return readStringFixed(length, DEFAULT_CHARSET);
    }
    
    /**
     * Reads an integer length-prefixed string.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @param charset character set to use when converting the bytes to string
     * @param align if greater than 0, read additional padding bytes so the total
     *              amount of bytes read is a multiple of this value
     * @return string
     * @throws IOException 
     */
    public String readStringInt(int limit, String charset, int align) throws IOException {
        int length = readInt();
        if (limit > 0 && length > limit) {
            return null;
        }
        
        String str = readStringFixed(length, charset);
        align(length, align);
        
        return str;
    }
    
    /**
     * Reads an integer length-prefixed string without alignment.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringInt(int limit, String charset) throws IOException {
        return readStringInt(limit, charset, 0);
    }
    
    /**
     * Reads an integer length-prefixed string without alignment, using the ASCII
     * charset.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @return string
     * @throws IOException 
     */
    public String readStringInt(int limit) throws IOException {
        return readStringInt(limit, DEFAULT_CHARSET);
    }
    
    /**
     * Reads an integer length-prefixed string without alignment, using the ASCII
     * charset and no limitation.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringInt() throws IOException {
        return readStringInt(0);
    }
    
    /**
     * Reads a short length-prefixed string.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @param charset character set to use when converting the bytes to string
     * @param align if greater than 0, read additional padding bytes so the total
     *              amount of bytes read is a multiple of this value
     * @return string
     * @throws IOException 
     */
    public String readStringShort(int limit, String charset, int align) throws IOException {
        int length = readUnsignedShort();
        if (limit > 0 && length > limit) {
            return null;
        }
        
        String str = readStringFixed(length, charset);
        align(length, align);
        
        return str;
    }
    
    /**
     * Reads a short length-prefixed string without alignment.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringShort(int limit, String charset) throws IOException {
        return readStringShort(limit, charset, 0);
    }
    
    
    /**
     * Reads a short length-prefixed string without alignment, using the ASCII
     * charset.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @return string
     * @throws IOException 
     */
    public String readStringShort(int limit) throws IOException {
        return readStringShort(limit, DEFAULT_CHARSET);
    }
    
    /**
     * Reads a short length-prefixed string without alignment, using the ASCII
     * charset and no limitation.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringShort() throws IOException {
        return readStringShort(0);
    }
    
    /**
     * Reads a byte length-prefixed string.
     * 
     * @param charset character set to use when converting the bytes to string
     * @param align if greater than 0, read additional padding bytes so the total
     *              amount of bytes read is a multiple of this value
     * @return string
     * @throws IOException 
     */
    public String readStringByte(String charset, int align) throws IOException {
        int length = readUnsignedByte();
        
        String str = readStringFixed(length, charset);
        align(length, align);
        
        return str;
    }
    
    /**
     * Reads a byte length-prefixed string without alignment.
     * 
     * @param limit if the string is longer than this value, return null
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringByte(String charset) throws IOException {
        return readStringByte(charset, 0);
    }
    
    /**
     * Reads a byte length-prefixed string without alignment, using the ASCII
     * charset.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringByte() throws IOException {
        return readStringByte(DEFAULT_CHARSET);
    }
    
    public void align(int length, int align) throws IOException {
        int rem = length % align;
        if (align > 0 && rem != 0) {
            skipBytes(align - rem);
        }
    }
}
