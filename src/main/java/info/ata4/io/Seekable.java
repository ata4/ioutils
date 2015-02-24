/*
 ** 2014 September 13
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
 * Interface for IO classes that provide random access with additional convenience
 * methods.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Seekable extends Positionable {
    
    public static enum Origin {
        BEGINNING, CURRENT, END
    }
    
    /**
     * Seek to the specified position relative to an origin.
     * 
     * @param where The position to seek to.
     * @param whence Origin point of the new position.
     * @throws IOException 
     */
    public void seek(long where, Origin whence) throws IOException;
    
    /**
     * Skips bytes to fit a specified data structure alignment.
     * The amount of bytes skipped equals <code>align - (position() % align)</code>.
     * If align smaller than 1, no bytes are skipped.
     * 
     * @param align data alignment length
     * @throws IOException 
     */
    public void align(int align) throws IOException;
    

    /**
     * Returns amount of remaining bytes that are available for reading or
     * writing from the current position.
     * 
     * @return
     * @throws IOException 
     */
    public long remaining() throws IOException;

    /**
     * Return true if there are remaining bytes to read or write.
     * 
     * @return
     * @throws IOException 
     */
    public boolean hasRemaining() throws IOException;
}
