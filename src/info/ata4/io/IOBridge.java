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

import info.ata4.io.socket.IOSocket;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Abstract IO bridge that combines a socket with various IO interfaces.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class IOBridge implements Swappable, Seekable, Closeable {
    
    private final IOSocket socket;
    private final ByteBuffer bb;
    private boolean swap;

    public IOBridge(IOSocket socket) {
        this.socket = socket;
        
        bb = socket.getByteBuffer();
        if (bb != null) {
            swap = bb.order() != ByteOrder.BIG_ENDIAN;
        }
    }
    
    public IOSocket getSocket() {
        return socket;
    }
    
    @Override
    public boolean isSwap() {
        return swap;
    }
    
    @Override
    public void setSwap(boolean swap) {
        this.swap = swap;
        if (bb != null) {
            bb.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
    }
    
    protected boolean isManualSwap() {
        return swap && bb == null;
    }
    
    public boolean isSeekable() {
        return socket.getSeekable() != null;
    }
    
    public Seekable getSeekable() {
        Seekable seekable = socket.getSeekable();
        if (seekable != null) {
            return seekable;
        } else {
            throw new UnsupportedOperationException("Seeking not supported");
        }
    }
    
    @Override
    public void seek(long where, SeekOrigin dir) throws IOException {
        getSeekable().seek(where, dir);
    }
    
    @Override
    public void position(long where) throws IOException {
        getSeekable().position(where);
    }

    @Override
    public long position() throws IOException {
        return getSeekable().position();
    }

    @Override
    public long capacity() throws IOException {
        return getSeekable().capacity();
    }

    @Override
    public long remaining() throws IOException {
        return getSeekable().remaining();
    }
    
    @Override
    public boolean hasRemaining() throws IOException {
        return getSeekable().hasRemaining();
    }
    
    @Override
    public void close() throws IOException {
        socket.close();
    }
}
