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

import info.ata4.io.util.HalfFloat;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DataWriter extends DataBridge implements DataOutput, StringOutput {
    
    public void writeStruct(Struct struct) throws IOException {
        struct.write(this);
    }
    
    public abstract OutputStream stream();
    
    ////////////////
    // DataOutput //
    ////////////////
    
    @Override
    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }
    
    @Override
    public void writeUnsignedByte(int v) throws IOException {
        writeByte((byte) (v & 0xff));
    }

    @Override
    public void writeUnsignedShort(int v) throws IOException {
        writeShort((short) (v & 0xffff));
    }

    @Override
    public void writeUnsignedInt(long v) throws IOException {
        writeInt((int) (v & 0xffffffff));
    }

    @Override
    public void writeUnsignedLong(BigInteger v) throws IOException {
        writeBytes(v.toByteArray());
    }


    @Override
    public void writeHalf(float f) throws IOException {
        int sval = HalfFloat.floatToIntBits(f);
        writeUnsignedShort(sval);
    }
    
    //////////////////
    // StringOutput //
    //////////////////
    
    @Override
    public void writeStringNull(String str, Charset charset) throws IOException {
        writeStringFixed(str, charset);
        writeUnsignedByte(0);
    }
    
    @Override
    public void writeStringNull(String str) throws IOException {
        writeStringNull(str, StandardCharsets.US_ASCII);
    }

    @Override
    public void writeStringFixed(String str, Charset charset) throws IOException {
        writeBytes(str.getBytes(charset));
    }
    
    @Override
    public void writeStringFixed(String str) throws IOException {
        writeStringFixed(str, StandardCharsets.US_ASCII);
    }
    
    @Override
    public void writeStringPrefixed(String str, Class<? extends Number> prefixType, Charset charset) throws IOException {
        int len = str.length();
        if (prefixType == Byte.TYPE) {
            writeUnsignedByte(len);
        } else if (prefixType == Short.TYPE) {
            writeUnsignedShort(len);
        } else if (prefixType == Integer.TYPE) {
            writeUnsignedInt(len);
        } else {
            throw new IllegalArgumentException("Wrong prefix data type");
        }
        
        writeStringFixed(str, charset);
    }
    
    @Override
    public void writeStringPrefixed(String str, Class<? extends Number> prefixType) throws IOException {
        writeStringPrefixed(str, prefixType, StandardCharsets.US_ASCII);
    }
}
