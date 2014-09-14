/*
 ** 2014 September 12
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.file.mmap;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * DataInput wrapper for byte buffers.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFileDataInput implements DataInput {
    
    private final MemoryMappedFile mmf;
    
    public MemoryMappedFileDataInput(MemoryMappedFile mmf) {
        this.mmf = mmf;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        try {
            mmf.get(b);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        try {
            mmf.get(b, off, len);
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int skipBytes(int n) throws IOException {
        try {
            n = Math.min(n, (int) Math.min(Integer.MAX_VALUE, mmf.getRemaining()));
            mmf.setPosition(mmf.getPosition() + n);
            return n;
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        try {
            return mmf.get() != 0;
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return mmf.get();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return readByte() & 0xff;
    }

    @Override
    public short readShort() throws IOException {
        try {
            return mmf.getShort();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    @Override
    public char readChar() throws IOException {
        try {
            return mmf.getChar();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return mmf.getInt();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return mmf.getLong();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public float readFloat() throws IOException {
        try {
            return mmf.getFloat();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            return mmf.getDouble();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public String readLine() throws IOException {
        try {
            StringBuilder sb = new StringBuilder();
            for (byte c = 0; mmf.hasRemaining() && c != '\n'; c = mmf.get()) {
                sb.append((char) c);
            }
            return sb.toString();
        } catch (BufferUnderflowException ex) {
            throw new EOFException();
        }
    }

    @Override
    public String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }
    
}
