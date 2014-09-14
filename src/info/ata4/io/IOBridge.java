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
 * Abstract IO bridge that combines a socket with various IO interfaces.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class IOBridge implements Swappable, Seekable, Closeable {
    
    private final IOSocket socket;
    private boolean swap;

    public IOBridge(IOSocket socket) {
        this.socket = socket;
        
        Swappable sw = socket.getSwappable();
        if (sw != null) {
            swap = sw.isSwap();
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
        Swappable sw = socket.getSwappable();
        if (sw != null) {
            sw.setSwap(swap);
        }
    }
    
    protected boolean isManualSwap() {
        return swap && socket.getSwappable() == null;
    }
    
    public boolean isPositionable() {
        return socket.getPositionable() != null;
    }
    
    public Positionable getPositionable() {
        Positionable positionable = socket.getPositionable();
        if (positionable != null) {
            return positionable;
        } else {
            throw new UnsupportedOperationException("Positioning not supported");
        }
    }
    
    @Override
    public void position(long where) throws IOException {
        getPositionable().position(where);
    }

    @Override
    public long position() throws IOException {
        return getPositionable().position();
    }

    @Override
    public long size() throws IOException {
        return getPositionable().size();
    }
    
    @Override
    public void seek(long where, Seekable.Origin whence) throws IOException {
        long pos = 0;
        switch (whence) {
            case BEGINNING:
                pos = where;
                break;

            case CURRENT:
                pos = position() + where;
                break;

            case END:
                pos = size() - where;
                break;
        }
        position(pos);
    }
    
    @Override
    public long remaining() throws IOException {
        return size() - position();
    }

    @Override
    public boolean hasRemaining() throws IOException {
        return remaining() > 0;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
