/*
 ** 2014 February 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.IOException;

/**
 * Basic Seekable implementation.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class SeekableImpl implements Seekable {
    
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
}
