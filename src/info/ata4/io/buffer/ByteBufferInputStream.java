/*
** 2011 April 5
**
** The author disclaims copyright to this source code.  In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
*/
package info.ata4.io.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;

/**
 * InputStream wrapper for byte buffers.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferInputStream extends InputStream {

    private final ByteBuffer buf;
    private int markPos;
    private int markReadLimit;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }
    
    public ByteBuffer getByteBuffer() {
        return buf;
    }
    
    @Override
    public int available() throws IOException {
        return buf.remaining();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readLimit) {
        buf.mark();
        markPos = buf.position();
        markReadLimit = readLimit;
    }

    @Override
    public synchronized void reset() throws IOException {
        // this doesn't really make sense for byte buffers, but it ensures
        // compliancy to InputStream.reset()
        if (buf.position() - markPos > markReadLimit) {
            throw new IOException("Invalid mark");
        }
        
        try {
            buf.reset();
        } catch (InvalidMarkException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized int read() throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        try {
            return 0xff & buf.get();
        } catch (BufferUnderflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if (!buf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, buf.remaining());

        try {
            buf.get(bytes, off, len);
        } catch (BufferUnderflowException ex) {
            throw new IOException(ex);
        }
        
        return len;
    }
}
