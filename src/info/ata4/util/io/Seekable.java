/*
 ** 2013 December 28
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

import java.io.IOException;

/**
 * Interface for IO classes that provide random access.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Seekable {

    /**
     * Seek to the specified position.
     * 
     * @param where The position to seek to.
     * @throws IOException 
     */
    public void seek(long where) throws IOException;

    /**
     * Obtain the current position.
     * 
     * @return
     * @throws IOException 
     */
    public long tell() throws IOException;
    
    /**
     * Returns the total capacity in bytes.
     * 
     * @return
     * @throws IOException 
     */
    public long length() throws IOException;
    
    /**
     * Returns amount of remaining bytes that are available for reading or
     * writing.
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
