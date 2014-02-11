/*
 ** 2013 December 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class RandomAccessFileWrapper implements DataInput, DataOutput, Closeable, Seekable {
    
    private final RandomAccessFile raf;

    public RandomAccessFileWrapper(RandomAccessFile raf) {
        this.raf = raf;
    }
    
    @Override
    public void readFully(byte[] b) throws IOException {
        raf.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        raf.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return raf.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return raf.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return raf.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return raf.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return raf.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return raf.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return raf.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return raf.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return raf.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return raf.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return raf.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return raf.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return raf.readUTF();
    }
    
    @Override
    public void write(int b) throws IOException {
        raf.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        raf.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        raf.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        raf.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        raf.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        raf.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        raf.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        raf.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        raf.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        raf.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        raf.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        raf.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        raf.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        raf.writeUTF(s);
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }
    
    @Override
    public void seek(long where, SeekOrigin whence) throws IOException {
        long pos = 0;
        switch (whence) {
            case BEGINNING:
                pos = where;
                break;
            
            case CURRENT:
                pos = position() + where;
                break;
                
            case END:
                pos = capacity() - where;
                break;
        }
        raf.seek(pos);
    }

    @Override
    public void position(long pos) throws IOException {
        raf.seek(pos);
    }

    @Override
    public long position() throws IOException {
        return raf.getFilePointer();
    }

    @Override
    public long capacity() throws IOException {
        return raf.length();
    }

    @Override
    public long remaining() throws IOException {
        return capacity() - position();
    }

    @Override
    public boolean hasRemaining() throws IOException {
        return remaining() > 0;
    }
}
