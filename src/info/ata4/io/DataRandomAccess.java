/*
 ** 2014 September 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import info.ata4.io.data.DataInputExtended;
import info.ata4.io.data.DataOutputExtended;
import info.ata4.io.socket.IOSocket;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * Combined data input and output extension with random access.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataRandomAccess extends IOBridge implements DataInputExtended, DataOutputExtended, ByteBufferReadable, ByteBufferWritable {

    private final DataInputReader reader;
    private final DataOutputWriter writer;

    public DataRandomAccess(IOSocket socket) {
        super(socket);
        reader = new DataInputReader(socket);
        writer = new DataOutputWriter(socket);
    }

    public DataInputReader getReader() {
        return reader;
    }

    public DataOutputWriter getWriter() {
        return writer;
    }

    @Override
    public void setSwap(boolean swap) {
        super.setSwap(swap);
        reader.setSwap(swap);
        writer.setSwap(swap);
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return reader.readUnsignedInt();
    }

    @Override
    public BigInteger readUnsignedLong() throws IOException {
        return reader.readUnsignedLong();
    }

    @Override
    public float readHalf() throws IOException {
        return reader.readHalf();
    }

    @Override
    public String readStringNull(int limit, Charset charset) throws IOException {
        return reader.readStringNull(limit, charset);
    }

    @Override
    public String readStringNull(int limit) throws IOException {
        return reader.readStringNull(limit);
    }

    @Override
    public String readStringNull() throws IOException {
        return reader.readStringNull();
    }

    @Override
    public String readStringPadded(int limit, Charset charset) throws IOException {
        return reader.readStringPadded(limit, charset);
    }

    @Override
    public String readStringPadded(int limit) throws IOException {
        return reader.readStringPadded(limit);
    }

    @Override
    public String readStringFixed(int length, Charset charset) throws IOException {
        return reader.readStringFixed(length, charset);
    }

    @Override
    public String readStringFixed(int length) throws IOException {
        return reader.readStringFixed(length);
    }

    @Override
    public String readStringInt(int limit, Charset charset) throws IOException {
        return reader.readStringInt(limit, charset);
    }

    @Override
    public String readStringInt(int limit) throws IOException {
        return reader.readStringInt(limit);
    }

    @Override
    public String readStringInt() throws IOException {
        return reader.readStringInt();
    }

    @Override
    public String readStringShort(int limit, Charset charset) throws IOException {
        return reader.readStringShort(limit, charset);
    }

    @Override
    public String readStringShort(int limit) throws IOException {
        return reader.readStringShort(limit);
    }

    @Override
    public String readStringShort() throws IOException {
        return reader.readStringShort();
    }

    @Override
    public String readStringByte(Charset charset) throws IOException {
        return reader.readStringByte(charset);
    }

    @Override
    public String readStringByte() throws IOException {
        return reader.readStringByte();
    }

    @Override
    public void readBuffer(ByteBuffer dst) throws IOException {
        reader.readBuffer(dst);
    }

    @Override
    public void readStruct(Struct struct) throws IOException {
        reader.readStruct(struct);
    }

    @Override
    public void align(int align) throws IOException {
        reader.align(align);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        reader.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        reader.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return reader.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return reader.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return reader.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return reader.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return reader.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return reader.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return reader.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return reader.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return reader.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return reader.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return reader.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return reader.readUTF();
    }
    
    @Override
    public void writeUnsignedByte(int v) throws IOException {
        writer.writeUnsignedByte(v);
    }

    @Override
    public void writeUnsignedShort(int v) throws IOException {
        writer.writeUnsignedShort(v);
    }

    @Override
    public void writeUnsignedInt(long v) throws IOException {
        writer.writeUnsignedInt(v);
    }

    @Override
    public void writeUnsignedLong(BigInteger v) throws IOException {
        writer.writeUnsignedLong(v);
    }

    @Override
    public void writeHalf(float f) throws IOException {
        writer.writeHalf(f);
    }

    @Override
    public void writeStringNull(String str, Charset charset) throws IOException {
        writer.writeStringNull(str, charset);
    }

    @Override
    public void writeStringNull(String str) throws IOException {
        writer.writeStringNull(str);
    }

    @Override
    public void writeStringPadded(String str, int padding, Charset charset) throws IOException {
        writer.writeStringPadded(str, padding, charset);
    }

    @Override
    public void writeStringPadded(String str, int padding) throws IOException {
        writer.writeStringPadded(str, padding);
    }

    @Override
    public void writeStringFixed(String str, Charset charset) throws IOException {
        writer.writeStringFixed(str, charset);
    }

    @Override
    public void writeStringFixed(String str) throws IOException {
        writer.writeStringFixed(str);
    }

    @Override
    public void writeStringInt(String str, Charset charset) throws IOException {
        writer.writeStringInt(str, charset);
    }

    @Override
    public void writeStringInt(String str) throws IOException {
        writer.writeStringInt(str);
    }

    @Override
    public void writeStringShort(String str, Charset charset) throws IOException {
        writer.writeStringShort(str, charset);
    }

    @Override
    public void writeStringShort(String str) throws IOException {
        writer.writeStringShort(str);
    }

    @Override
    public void writeStringByte(String str, Charset charset) throws IOException {
        writer.writeStringByte(str, charset);
    }

    @Override
    public void writeStringByte(String str) throws IOException {
        writer.writeStringByte(str);
    }

    @Override
    public void writeBuffer(ByteBuffer src) throws IOException {
        writer.writeBuffer(src);
    }

    @Override
    public void writeStruct(Struct struct) throws IOException {
        writer.writeStruct(struct);
    }

    @Override
    public void write(int b) throws IOException {
        writer.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        writer.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writer.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        writer.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        writer.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        writer.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        writer.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        writer.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        writer.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writer.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writer.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        writer.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        writer.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        writer.writeUTF(s);
    }
    
    @Override
    public void fill(int n, byte b) throws IOException {
        writer.fill(n, b);
    }
}
