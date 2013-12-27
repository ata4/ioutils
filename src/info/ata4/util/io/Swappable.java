/*
 ** 2013 December 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

/**
 * Interface for IO classes that can dynamically swap the byte order.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Swappable {
    
    /**
     * Returns true if all incoming data are read in reverse byte order and
     * are assumed to have little endian instead of big endian.
     *
     * @return true if byte swapping is active
     */
    public boolean isSwap();
    
    /**
     * Changes the byte swapping flag. If true, all incoming data are read in
     * reverse byte order and are assumed to have little endian instead of
     * big endian.
     * 
     * @param swap new byte swapping flag
     */
    public void setSwap(boolean swap);
}
