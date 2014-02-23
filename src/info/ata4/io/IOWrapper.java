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

/**
 * Abstract class for wrapping DataInput or DataOutput objects (or any other
 * objects, theoretically). Also implements various interfaces and does feature
 * checking.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class IOWrapper implements Swappable, Seekable, Closeable {
    
    private final IOSocket socket;

    public IOWrapper(IOSocket socket) {
        this.socket = socket;
    }
    
    public IOSocket getSocket() {
        return socket;
    }
    
    public boolean isSwappable() {
        return socket.getSwappable() != null;
    }
    
    public Swappable getSwappable() {
        Swappable swappable = socket.getSwappable();
        if (swappable != null) {
            return swappable;
        } else {
            throw new UnsupportedOperationException("Byte swapping not supported");
        }
    }
    
    @Override
    public boolean isSwap() {
        return getSwappable().isSwap();
    }

    @Override
    public void setSwap(boolean swap) {
        getSwappable().setSwap(swap);
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
