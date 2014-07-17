/*
 ** 2014 January 12
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Interface for extended DataInput methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface DataInputExtended extends DataInput {
    
    /**
     * Reads a 32 bit unsigned integer number and returns it as long.
     * 
     * @return unsigned integer as long
     * @throws IOException 
     */
    public long readUnsignedInt() throws IOException;

    /**
     * Reads a 64 bit unsigned long number and returns it as BigInteger.
     * 
     * @return unsigned long as BigInteger
     * @throws IOException 
     */
    public BigInteger readUnsignedLong() throws IOException;

    /**
     * Reads a half-precision 16 bit floating-point number according to
     * IEEE 754-2008. The result is returned as normal 32 bit float.
     * 
     * @return half-precision float as full-precision float
     * @throws IOException 
     */
    public float readHalf() throws IOException;

    /**
     * Reads a null-terminated string without byte padding.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringNull(int limit, Charset charset) throws IOException;

    /**
     * Reads a null-terminated string without byte padding, using the ASCII charset.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @return string
     * @throws IOException 
     */
    public String readStringNull(int limit) throws IOException;

    /**
     * Reads a null-terminated string without byte padding, using the ASCII
     * charset and with a limit of 256 bytes.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringNull() throws IOException;
    
    /**
     * Reads a null-terminated string with byte padding.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringPadded(int limit, Charset charset) throws IOException;
    
    /**
     * Reads a null-terminated string with byte padding, using the ASCII charset.
     * 
     * @param limit maximum amount of bytes to read before truncation
     * @return string
     * @throws IOException 
     */
    public String readStringPadded(int limit) throws IOException;

   /**
     * Reads a fixed size string.
     * 
     * @param length total length of the string
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringFixed(int length, Charset charset) throws IOException;

    /**
     * Reads a fixed size string using the ASCII charset.
     * 
     * @param length total length of the string
     * @return string
     * @throws IOException 
     */
    public String readStringFixed(int length) throws IOException;

    /**
     * Reads an integer length-prefixed string without alignment.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringInt(int limit, Charset charset) throws IOException;

    /**
     * Reads an integer length-prefixed string without alignment, using the ASCII
     * charset.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @return string
     * @throws IOException 
     */
    public String readStringInt(int limit) throws IOException;

    /**
     * Reads an integer length-prefixed string without alignment, using the ASCII
     * charset and no limitation.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringInt() throws IOException;

    /**
     * Reads a short length-prefixed string without alignment.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringShort(int limit, Charset charset) throws IOException;

    /**
     * Reads a short length-prefixed string without alignment, using the ASCII
     * charset.
     * 
     * @param limit if greater than zero, return null if the string is longer
     *              than this limit
     * @return string
     * @throws IOException 
     */
    public String readStringShort(int limit) throws IOException;
    
    /**
     * Reads a short length-prefixed string without alignment, using the ASCII
     * charset and no limitation.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringShort() throws IOException;

    /**
     * Reads a byte length-prefixed string without alignment.
     * 
     * @param charset character set to use when converting the bytes to string
     * @return string
     * @throws IOException 
     */
    public String readStringByte(Charset charset) throws IOException;
    
    /**
     * Reads a byte length-prefixed string without alignment, using the ASCII
     * charset.
     * 
     * @return string
     * @throws IOException 
     */
    public String readStringByte() throws IOException;
    
    /**
     * Reads a sequence of bytes from this data input into the given buffer.
     * 
     * @param dst The buffer into which bytes are to be transferred
     * @throws IOException 
     */
    public void readBuffer(ByteBuffer dst) throws IOException;
    
    public void readStruct(Struct struct) throws IOException;

    /**
     * Skips bytes to fit a specified data structure alignment.
     * The amount of bytes skipped equals <code>align - (position() % align)</code>.
     * If align smaller than 1, no bytes are skipped.
     * 
     * @param align data alignment length
     * @throws IOException 
     */
    public void align(int align) throws IOException;
}
