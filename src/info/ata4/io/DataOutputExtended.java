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

import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.Charset;

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

    public void writeStringNull(String str, Charset charset) throws IOException;

    public void writeStringNull(String str) throws IOException;
    
    public void writeStringPadded(String str, int padding, Charset charset) throws IOException;
    
    public void writeStringPadded(String str, int padding) throws IOException;

    public void writeStringFixed(String str, Charset charset) throws IOException;

    public void writeStringFixed(String str) throws IOException;
    
    public void writeStringInt(String str, Charset charset) throws IOException;
    
    public void writeStringInt(String str) throws IOException;
    
    public void writeStringShort(String str, Charset charset) throws IOException;
    
    public void writeStringShort(String str) throws IOException;
    
    public void writeStringByte(String str, Charset charset) throws IOException;
    
    public void writeStringByte(String str) throws IOException;
    
    public void writeStruct(Struct struct) throws IOException;

    /**
     * Writes a number of fill bytes of the supplied type.
     *
     * @param      n   the number of bytes to fill
     * @param      b   value of the fill bytes
     * @exception  IOException   if an I/O error occurs.
     */
    public void fill(int n, byte b) throws IOException;

    /**
     * Skips bytes to fit a specified data structure alignment.
     * The amount of bytes skipped equals <code>align - (position() % align)</code>.
     * If align smaller than 1, no bytes are skipped. Skipped bytes are written
     * as zeros.
     * 
     * @param align data alignment length
     * @throws IOException 
     */
    public void align(int align) throws IOException;
}