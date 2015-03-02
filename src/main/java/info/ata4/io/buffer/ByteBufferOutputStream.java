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
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * OutputStream adapter for a ByteBuffer.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferOutputStream extends OutputStream {

    private final ByteBuffer buf;

    public ByteBufferOutputStream(ByteBuffer buf) {
        if (buf.isReadOnly()) {
            throw new IllegalArgumentException("Buffer is read-only");
        }

        this.buf = buf;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            buf.put((byte) b);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized void write(byte[] bytes, int off, int len) throws IOException {
        try {
            buf.put(bytes, off, len);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }
}
