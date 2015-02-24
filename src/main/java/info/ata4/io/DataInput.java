/*
 ** 2015 February 20
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Custom DataInput interface with better handling for unsigned data types and
 * configurable byte order.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface DataInput extends Swappable {
    
    public void readBytes(byte b[]) throws IOException;
    
    public void readBytes(byte b[], int off, int len) throws IOException;
    
    public void readBuffer(ByteBuffer buf) throws IOException;
    
    public byte readByte() throws IOException;
    
    public int readUnsignedByte() throws IOException;
    
    public boolean readBoolean() throws IOException;
    
    public char readChar() throws IOException;
    
    public short readShort() throws IOException;
    
    public int readUnsignedShort() throws IOException;
    
    public int readInt() throws IOException;
    
    /**
     * Reads a 32 bit unsigned integer number and returns it as long.
     * 
     * @return unsigned integer as long
     * @throws IOException 
     */
    public long readUnsignedInt() throws IOException;
    
    public long readLong() throws IOException;
    
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
    
    public float readFloat() throws IOException;
    
    public double readDouble() throws IOException;
}
