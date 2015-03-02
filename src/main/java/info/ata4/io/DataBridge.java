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

import info.ata4.io.buffer.source.BufferedSource;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Base class for both DataReader and DataWriter.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DataBridge implements Seekable, Closeable, Swappable {
    
    protected final BufferedSource buf;
    
    public DataBridge(BufferedSource buf) {
        this.buf = buf;
    }

    ///////////////
    // Swappable //
    ///////////////
    
    @Override
    public ByteOrder order() {
        return buf.order();
    }
    
    @Override
    public void order(ByteOrder order) {
        buf.order(order);
    }
    
    //////////////////
    // Positionable //
    //////////////////
    
    @Override
    public long position() throws IOException {
        return buf.position();
    }
    
    @Override
    public void position(long newPos) throws IOException {
        buf.position(newPos);
    }
    
    @Override
    public long size() throws IOException {
        return buf.size();
    }
    
    //////////////
    // Seekable //
    //////////////

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
    public void align(int align) throws IOException {
        if (align < 0) {
            throw new IllegalArgumentException();
        } else if (align == 0) {
            return;
        }
        
        long pos = position();
        long rem = pos % align;
        if (rem != 0) {
            position(pos + align - rem);
        }
    }
    
    ///////////////
    // Closeable //
    ///////////////

    @Override
    public void close() throws IOException {
        buf.close();
    }
}
