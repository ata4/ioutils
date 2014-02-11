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

import java.io.Closeable;
import java.io.IOException;

/**
 * Abstract class for wrapping DataInput or DataOutput objects (or any other
 * objects, theoretically). Also implements various interfaces and does feature
 * checking.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class IOWrapper<T> implements Swappable, Seekable, Closeable {
    
    private final T wrapped;
    private final Swappable swappable;
    private final Seekable seekable;
    private final Closeable closeable;

    public IOWrapper(T wrapped) {
        this.wrapped = wrapped;
        swappable = wrapped instanceof Swappable ? (Swappable) wrapped : null;
        seekable = wrapped instanceof Seekable ? (Seekable) wrapped : null;
        closeable = wrapped instanceof Closeable ? (Closeable) wrapped : null;
    }
    
    public T getWrapped() {
        return wrapped;
    }
    
    public boolean isSwappable() {
        return swappable != null;
    }
    
    public Swappable getSwappable() {
        if (isSwappable()) {
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
        return seekable != null;
    }
    
    public Seekable getSeekable() {
        if (isSeekable()) {
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
    
    public boolean isCloseable() {
        return closeable != null;
    }
    
    public Closeable getCloseable() {
        if (isCloseable()) {
            return closeable;
        } else {
            throw new UnsupportedOperationException("Closing not supported");
        }
    }
    
    @Override
    public void close() throws IOException {
        getCloseable().close();
    }
}
