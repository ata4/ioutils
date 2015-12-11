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
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataWriter extends DataBridge implements DataOutput, StringOutput {
    
    public DataWriter(BufferedSource buf) {
        super(buf);
    }
    
    public void writeStruct(Struct struct) throws IOException {
        struct.write(this);
    }
    
    public OutputStream stream() {
        return Channels.newOutputStream(new BufferedSourceChannel(buf));
    }
    
    ////////////////
    // DataOutput //
    ////////////////
    
    @Override
    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        buf.requestWrite(len).put(b, off, len);
    }
    
    @Override
    public void writeBuffer(ByteBuffer src) throws IOException {
        while (src.hasRemaining() && buf.write(src) > 0);
        if (src.hasRemaining()) {
            throw new EOFException();
        }
    }
    
    @Override
    public void writeByte(byte b) throws IOException {
        buf.requestWrite(1).put(b);
    }
    
    @Override
    public void writeBoolean(boolean v) throws IOException {
        buf.requestWrite(1).put((byte) (v ? 1 : 0));
    }

    @Override
    public void writeShort(short v) throws IOException {
        buf.requestWrite(2).putShort(v);
    }

    @Override
    public void writeChar(char v) throws IOException {
        buf.requestWrite(2).putChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        buf.requestWrite(4).putInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        buf.requestWrite(8).putLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        buf.requestWrite(4).putFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        buf.requestWrite(8).putDouble(v);
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
        writeInt((int) (v & 0xffffffffl));
    }

    @Override
    public void writeUnsignedLong(BigInteger v) throws IOException {
        writeLong(v.longValue());
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
    public void writeStringFixed(String str, int length, Charset charset) throws IOException {
        byte[] raw = str.getBytes(charset);
        writeBytes(raw);
        int padding = length - raw.length;
        for (int i = 0; i < padding; i++) {
            writeUnsignedByte(0);
        }
    }
    
    @Override
    public void writeStringFixed(String str, int length) throws IOException {
        writeStringFixed(str, length, StandardCharsets.US_ASCII);
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
    
    @Override
    public void align(int align) throws IOException {
        // avoid positioning and write null bytes, since it's pretty slow to
        // flush the buffer every time after correcting the position
        long pos = position();
        long rem = pos % align;
        if (rem != 0) {
            int pad = (int) (align - rem);
            while (pad > 0) {
                int padWrite = Math.min(4096, pad);
                writeBytes(new byte[padWrite]);
                pad -= padWrite;
            }
        }
    }
}
