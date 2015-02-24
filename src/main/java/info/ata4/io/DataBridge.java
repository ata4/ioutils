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
 * Base class for both DataReader and DataWriter.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class DataBridge implements Seekable, Closeable {
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public long position() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void position(long newPos) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public long size() throws IOException {
        throw new UnsupportedOperationException();
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
    public void align(int align) throws IOException {
        if (align > 0) {
            int rem = (int) (position() % align);
            if (rem != 0) {
                seek(align - rem, Seekable.Origin.CURRENT);
            }
        }
    }
}
