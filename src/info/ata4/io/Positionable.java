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

import java.io.IOException;

/**
 * Interface for IO classes that provide random access.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Positionable {
    
    /**
     * Sets a new absolute position.
     * 
     * @param where The position to seek to.
     * @throws IOException 
     */
    public void position(long where) throws IOException;

    /**
     * Obtain the current position.
     * 
     * @return
     * @throws IOException 
     */
    public long position() throws IOException;
    
    /**
     * Returns the total size in bytes.
     * 
     * @return
     * @throws IOException 
     */
    public long size() throws IOException;

}
