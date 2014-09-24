/*
 ** 2014 September 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface ByteBufferReadable {
    
    /**
     * Reads a sequence of bytes from this data input into the given buffer.
     * 
     * @param dst The buffer into which bytes are to be transferred
     * @throws IOException 
     */
    void readBuffer(ByteBuffer dst) throws IOException;
}
