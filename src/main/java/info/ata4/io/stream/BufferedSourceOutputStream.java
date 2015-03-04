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
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedSourceOutputStream extends OutputStream {
    
    private final BufferedSource buf;
    
    public BufferedSourceOutputStream(BufferedSource buf) {
        if (!buf.canWrite()) {
            throw new IllegalArgumentException("Source is not writable");
        }
        this.buf = buf;
    }

    @Override
    public void write(int b) throws IOException {
        buf.requestWrite(1).put((byte) b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (len > buf.bufferSize()) {
            ByteBuffer bb = ByteBuffer.wrap(b, off, len);
            while (bb.hasRemaining()) {
                buf.write(bb);
            }
        } else {
            buf.requestWrite(len).put(b, off, len);
        }
    }
}
