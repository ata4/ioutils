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

import info.ata4.io.Seekable;
import info.ata4.io.buffer.MemoryMappedFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFileSocket extends IOSocket {
    
    private final MemoryMappedFile mmfile;

    public MemoryMappedFileSocket(MemoryMappedFile mmfile) {
        this.mmfile = mmfile;
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
    protected Seekable newSeekable() {
        return mmfile;
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
}
