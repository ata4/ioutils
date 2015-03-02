/*
 ** 2015 March 02
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.stream;

import info.ata4.io.buffer.source.BufferedSource;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedSourceInputStream extends InputStream {
    
    private final BufferedSource buf;
    
    public BufferedSourceInputStream(BufferedSource buf) {
        if (!buf.canRead()) {
            throw new IllegalArgumentException("Source is not readable");
        }
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        try {
            return buf.requestRead(1).get() & 0xff;
        } catch (EOFException ex) {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return buf.read(ByteBuffer.wrap(b, off, len));
    }

    @Override
    public long skip(long n) throws IOException {
        try {
            long posOld = buf.position();
            long posNew = Math.min(posOld + n, buf.size());
            buf.position(posNew);
            return posNew - posOld;
        } catch (UnsupportedOperationException ex) {
            return super.skip(n);
        }
    }
}
