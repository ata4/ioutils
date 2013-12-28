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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import org.apache.commons.io.EndianUtils;

/**
 * DataOutput extension for more data access methods.
 * 
 * TODO: add missing corresponding DataInputReader methods
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataOutputWriter extends DataOutputWrapper implements Swappable {
    
    private static final String DEFAULT_CHARSET = "ASCII";
    private boolean swap;
    
    public DataOutputWriter(DataOutput out) {
        super(out);
    }
    
    public DataOutputWriter(RandomAccessFile raf) {
        super(new RandomAccessFileWrapper(raf));
    }
    
    public DataOutputWriter(OutputStream is) {
        super(new DataOutputStream(is));
    }
    
    public DataOutputWriter(ByteBuffer bb) {
        super(new ByteBufferOutput(bb));
    }
    
    @Override
    public boolean isSwap() {
        if (super.isSwappable()) {
            return super.isSwap();
        } else {
            return swap;
        }
    }

    @Override
    public void setSwap(boolean swap) {
        if (super.isSwappable()) {
            super.setSwap(swap);
        } else {
            this.swap = swap;
        }
    }

    @Override
    public boolean isSwappable() {
        // supports manual swapping using EndianUtils if required
        return true;
    }
    
    @Override
    public void writeShort(int v) throws IOException {
        if (swap) {
            v = EndianUtils.swapInteger(v);
        }
        super.writeShort(v);
    }
    
    @Override
    public void writeInt(int v) throws IOException {
        if (swap) {
            v = EndianUtils.swapInteger(v);
        }
        super.writeInt(v);
    }
    
    @Override
    public void writeDouble(double v) throws IOException {
        if (swap) {
            v = EndianUtils.swapDouble(v);
        }
        super.writeDouble(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        if (swap) {
            v = EndianUtils.swapFloat(v);
        }
        super.writeFloat(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (swap) {
            v = EndianUtils.swapLong(v);
        }
        super.writeLong(v);
    }
    
    /**
     * Writes a float as half-precision 16 bit floating-point number according to
     * IEEE 754-2008.
     * 
     * @param f float value
     * @throws IOException 
     */
    public void writeHalf(float f) throws IOException {
        int sval = HalfFloat.floatToIntBits(f);
        writeShort(sval);
    }
    
    public void writeStringFixed(String str, String charset) throws IOException {
        write(str.getBytes(charset));
    }
    
    public void writeStringFixed(String str) throws IOException {
        writeStringFixed(str, DEFAULT_CHARSET);
    }

    public void writeStringNull(String str, String charset) throws IOException {
        writeStringFixed(str, charset);
        writeByte(0);
    }
    
    public void writeStringNull(String str) throws IOException {
        writeStringNull(str, DEFAULT_CHARSET);
    }
    
    public void skipBytes(int n) throws IOException {
        write(new byte[n]);
    }

    public void align(int length, int align) throws IOException {
        int rem = length % align;
        if (align > 0 && rem != 0) {
            skipBytes(align - rem);
        }
    }
}
