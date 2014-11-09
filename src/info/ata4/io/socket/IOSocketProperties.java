/*
 ** 2014 November 08
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

/**
 * Socket properties metadata class.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface IOSocketProperties {
    
    /**
     * Returns true if the socket can be read from.
     * 
     * @return readable flag
     */
    public boolean isReadable();

    /**
     * Returns true if the socket can be written to.
     * 
     * @return writable flag
     */
    public boolean isWritable();

    /**
     * Returns true if the socket is writable and can be written beyond the reported
     * size.
     * 
     * @return growable flag
     */
    public boolean isGrowable();

    /**
     * Returns true if the socket is a stream and can only progress forwards.
     * 
     * @return streaming flag
     */
    public boolean isStreaming();
    
    /**
     * Returns true if all I/O operations on this socket are buffered.
     * 
     * @return buffering flag
     */
    public boolean isBuffered();
}
