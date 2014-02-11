/*
 ** 2013 June 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class ByteBufferWrapper implements Swappable, Seekable {
    
    protected final ByteBuffer buf;

    public ByteBufferWrapper(ByteBuffer buf) {
        this.buf = buf;
    }
    
    public ByteBuffer getBuffer() {
        return buf;
    }
    
    @Override
    public boolean isSwap() {
        return buf.order() != ByteOrder.BIG_ENDIAN;
    }

    @Override
    public void setSwap(boolean swap) {
        buf.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
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
        position(pos);
    }
    
    @Override
    public void position(long pos) throws IOException {
        if (pos > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Pointer is too large (> " + Integer.MAX_VALUE + ")");
        }
        buf.position((int) pos);
    }

    @Override
    public long position() throws IOException {
        return buf.position();
    }

    @Override
    public long capacity() throws IOException {
        return buf.capacity();
    }

    @Override
    public boolean hasRemaining() throws IOException {
        return buf.hasRemaining();
    }

    @Override
    public long remaining() throws IOException {
        return buf.remaining();
    }
}
