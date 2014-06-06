/*
 ** 2014 May 20
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Input stream for reading integers with variable bit lengths.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BitInputStream extends InputStream {
    
    private final InputStream is;
    private int bitBuffer;
    private int bitCount;
    private int bits = 8;

    public BitInputStream(InputStream is) {
        this.is = is;
    }
    
    public int getBitLength() {
        return bits;
    }

    public void setBitLength(int bits) {
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException();
        }
        
        this.bits = bits;
    }

    @Override
    public int read() throws IOException {
        // pass-through when reading aligned octets
        if (bits == 8 && bitCount == 0) {
            return is.read();
        }
        
        while (bitCount < bits) {
            int b = is.read();
            if (b == -1) {
                return b;
            }

            bitBuffer |= b << bitCount;
            bitCount += 8;
        }

        int code = bitBuffer;   
        if (bitCount != 32) {
            code &= (1 << bits) - 1;
        }
        
        bitBuffer >>= bits;
        bitCount -= bits;
        return code;
    }
    
    public int read(int bits) throws IOException {
        setBitLength(bits);
        return read();
    }
    
    @Override
    public int read(byte[] b) throws IOException {
        checkByteArrayOp();
        return super.read(b);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        checkByteArrayOp();
        return super.read(b, off, len);
    }

    private void checkByteArrayOp() {
        // byte array operations won't work correctly with more than 8 bits!
        if (bits > 8) {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public void close() throws IOException {
        is.close();
    }
}
