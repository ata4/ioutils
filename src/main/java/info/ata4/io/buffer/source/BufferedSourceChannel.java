/*
 ** 2015 March 06
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer.source;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedSourceChannel implements ByteChannel {
    
    private final BufferedSource buf;
    private boolean closed;
    
    public BufferedSourceChannel(BufferedSource buf) {
        this.buf = buf;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        
        if (!buf.canRead()) {
            throw new NonReadableChannelException();
        }
        
        return buf.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (closed) {
            throw new ClosedChannelException();
        }
        
        if (!buf.canWrite()) {
            throw new NonWritableChannelException();
        }
        
        return buf.write(src);
    }
    
    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        buf.flush();
    }
}
