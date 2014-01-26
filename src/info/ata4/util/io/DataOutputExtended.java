/*
 ** 2014 January 12
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface for extended DataOutput methods.
 * 
  * TODO: incomplete, add corresponding DataInputReader methods
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface DataOutputExtended extends DataOutput {
    
    /**
     * Writes a float as half-precision 16 bit floating-point number according to
     * IEEE 754-2008.
     * 
     * @param f float value
     * @throws IOException 
     */
    public void writeHalf(float f) throws IOException;

    public void writeStringNull(String str, String charset) throws IOException;

    public void writeStringNull(String str) throws IOException;
    
    public void writeStringPadded(String str, int padding, String charset) throws IOException;
    
    public void writeStringPadded(String str, int padding) throws IOException;

    public void writeStringFixed(String str, String charset) throws IOException;

    public void writeStringFixed(String str) throws IOException;
    
    public void writeStringInt(String str, String charset) throws IOException;
    
    public void writeStringInt(String str) throws IOException;
    
    public void writeStringShort(String str, String charset) throws IOException;
    
    public void writeStringShort(String str) throws IOException;
    
    public void writeStringByte(String str, String charset) throws IOException;
    
    public void writeStringByte(String str) throws IOException;

    /**
     * Makes an attempt to skip over
     * <code>n</code> bytes of data in the output
     * stream, writing null bytes in place of the skipped bytes.
     * However, it may skip over some smaller number of
     * bytes, possibly zero. This may result from
     * any of a number of conditions; reaching
     * end of file before <code>n</code> bytes
     * have been skipped is only one possibility.
     * This method never throws an <code>EOFException</code>.
     * The actual number of bytes skipped is returned.
     *
     * @param      n   the number of bytes to be skipped.
     * @return     the number of bytes actually skipped.
     * @exception  IOException   if an I/O error occurs.
     */
    public void skipBytes(int n) throws IOException;

    /**
     * Skips bytes to fit a specified data structure alignment.
     * The amount of bytes skipped equals <code>align - (length % align)</code>.
     * If <code>align</code> smaller than 1, no bytes are skipped.
     * 
     * @param length data length to apply the alignment on
     * @param align data alignment length
     * @throws IOException 
     */
    public void align(int length, int align) throws IOException;
}