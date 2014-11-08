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

import info.ata4.io.data.DataOutputExtended;
import info.ata4.io.socket.IOSocket;
import info.ata4.io.util.HalfFloat;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.commons.io.EndianUtils;

/**
 * DataOutput extension for more data access methods.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataOutputWriter extends IOBridge implements DataOutputExtended, ByteBufferWritable {
    
    // Charset.defaultCharset() is platform dependent and should not be used.
    // This includes the omitted charset parameter for String.getBytes().
    private static final Charset DEFAULT_CHARSET = Charset.forName("ASCII");
    
    private final DataOutput out;

    public DataOutputWriter(IOSocket socket) {
        super(socket);
        this.out = socket.getDataOutput();
    }
    
    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        out.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        out.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (isManualSwap()) {
            v = EndianUtils.swapShort((short) v);
        }
        out.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        out.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (isManualSwap()) {
            v = EndianUtils.swapInteger(v);
        }
        out.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (isManualSwap()) {
            v = EndianUtils.swapLong(v);
        }
        out.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        if (isManualSwap()) {
            // NOTE: don't use writeFloat() plus EndianUtils.swapFloat() here!
            writeInt(Float.floatToRawIntBits(v));
        } else {
            out.writeFloat(v);
        }
    }

    @Override
    public void writeDouble(double v) throws IOException {
        if (isManualSwap()) {
            // NOTE: don't use writeDouble() plus EndianUtils.swapDouble() here!
            writeLong(Double.doubleToRawLongBits(v));
        } else {
            out.writeDouble(v);
        }
    }

    @Override
    public void writeBytes(String s) throws IOException {
        out.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        out.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        out.writeUTF(s);
    }
    
    @Override
    public void writeUnsignedByte(int v) throws IOException {
        writeByte(v & 0xff);
    }

    @Override
    public void writeUnsignedShort(int v) throws IOException {
        writeShort(v & 0xffff);
    }

    @Override
    public void writeUnsignedInt(long v) throws IOException {
        writeInt((int) (v & 0xffffffff));
    }

    @Override
    public void writeUnsignedLong(BigInteger v) throws IOException {
        write(v.toByteArray());
    }
    
    @Override
    public void writeHalf(float f) throws IOException {
        int sval = HalfFloat.floatToIntBits(f);
        writeShort(sval);
    }

    @Override
    public void writeStringNull(String str, Charset charset) throws IOException {
        writeStringFixed(str, charset);
        writeByte(0);
    }
    
    @Override
    public void writeStringNull(String str) throws IOException {
        writeStringNull(str, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeStringPadded(String str, int padding, Charset charset) throws IOException {
        int nullBytes = padding - str.length();
        if (nullBytes < 0) {
            throw new IllegalArgumentException("Invalid padding");
        }
        
        writeStringFixed(str, charset);
        fill(nullBytes, (byte) 0);
    }

    @Override
    public void writeStringPadded(String str, int padding) throws IOException {
        writeStringPadded(str, padding, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeStringFixed(String str, Charset charset) throws IOException {
        write(str.getBytes(charset));
    }
    
    @Override
    public void writeStringFixed(String str) throws IOException {
        writeStringFixed(str, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeStringInt(String str, Charset charset) throws IOException {
        writeInt(str.length());
        writeStringFixed(str, charset);
    }

    @Override
    public void writeStringInt(String str) throws IOException {
        writeStringInt(str, DEFAULT_CHARSET);
    }

    @Override
    public void writeStringShort(String str, Charset charset) throws IOException {
        if (str.length() > 0xffff) {
            throw new IllegalArgumentException("String is too long");
        }
        
        writeShort(str.length());
        writeStringFixed(str, charset);
    }

    @Override
    public void writeStringShort(String str) throws IOException {
        writeStringShort(str, DEFAULT_CHARSET);
    }

    @Override
    public void writeStringByte(String str, Charset charset) throws IOException {
        if (str.length() > 0xff) {
            throw new IllegalArgumentException("String is too long");
        }
        
        writeByte(str.length());
        writeStringFixed(str, charset);
    }

    @Override
    public void writeStringByte(String str) throws IOException {
        writeStringByte(str, DEFAULT_CHARSET);
    }
    
    @Override
    public void writeBuffer(ByteBuffer src) throws IOException {
        ByteBufferWritable writable = getSocket().getByteBufferWritable();
        if (writable != null) {
            writable.writeBuffer(src);
        } else {
            while (src.hasRemaining()) {
                writeByte(src.get());
            }
        }
    }
    
    @Override
    public void writeStruct(Struct struct) throws IOException {
        struct.write(this);
    }
    
    @Override
    public void fill(int n, byte b) throws IOException {
        for (int i = 0; i < n; i++) {
            writeByte(b);
        }
    }
    
    @Override
    public void align(int align) throws IOException {
        if (align > 0) {
            int rem = (int) (position() % align);
            if (rem != 0) {
                fill(align - rem, (byte) 0);
            }
        }
    }
}
