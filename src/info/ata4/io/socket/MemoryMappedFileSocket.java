/*
 ** 2014 February 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.SeekOrigin;
import info.ata4.io.Seekable;
import info.ata4.io.SeekableImpl;
import info.ata4.io.Swappable;
import info.ata4.io.buffer.MemoryMappedFile;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteOrder;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFileSocket extends IOSocket {
    
    private final MemoryMappedFile mmfile;

    public MemoryMappedFileSocket(MemoryMappedFile mmfile) {
        this.mmfile = mmfile;
        setCanRead(true);
        setCanWrite(!mmfile.isReadOnly());
    }

    @Override
    protected InputStream newInputStream() {
        return new MemoryMappedInputStream();
    }
    
    @Override
    protected OutputStream newOutputStream() {
        return new ByteBufferOutputStream();
    }

    @Override
    public DataInput getDataInput() {
        return mmfile;
    }

    @Override
    public DataOutput getDataOutput() {
        return mmfile;
    }
    
    @Override
    protected Swappable newSwappable() {
        return new ByteBufferSwappable();
    }

    @Override
    protected Seekable newSeekable() {
        return new MemoryMappedSeekable();
    }

    private class MemoryMappedInputStream extends InputStream {

        @Override
        public synchronized int read() throws IOException {
            if (!mmfile.hasRemaining()) {
                return -1;
            }

            try {
                return 0xff & mmfile.get();
            } catch (BufferUnderflowException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public synchronized int read(byte[] bytes, int off, int len) throws IOException {
            if (!mmfile.hasRemaining()) {
                return -1;
            }

            if (mmfile.remaining() < Integer.MAX_VALUE) {
                len = Math.min(len, (int) mmfile.remaining());
            }

            try {
                mmfile.get(bytes, off, len);
            } catch (BufferUnderflowException ex) {
                throw new IOException(ex);
            }

            return len;
        }
    }
    
    private class ByteBufferOutputStream extends OutputStream {

        @Override
        public synchronized void write(int b) throws IOException {
            try {
                mmfile.put((byte) b);
            } catch (BufferOverflowException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public synchronized void write(byte[] bytes, int off, int len) throws IOException {
            try {
                mmfile.put(bytes, off, len);
            } catch (BufferOverflowException ex) {
                throw new IOException(ex);
            }
        }
    }
    
    private class ByteBufferSwappable implements Swappable {

        @Override
        public boolean isSwap() {
            return mmfile.order() != ByteOrder.BIG_ENDIAN;
        }

        @Override
        public void setSwap(boolean swap) {
            mmfile.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
    }
    
    private class MemoryMappedSeekable extends SeekableImpl {

        @Override
        public void position(long where) throws IOException {
            mmfile.position(where);
        }

        @Override
        public long position() throws IOException {
            return mmfile.position();
        }

        @Override
        public long capacity() throws IOException {
            return mmfile.capacity();
        }

    }
}
