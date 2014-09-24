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
public interface ByteBufferWritable {
    
    /**
     * Writes a sequence of bytes to this data output from the given buffer.
     * 
     * @param src The buffer from which bytes are to be retrieved
     * @throws IOException 
     */
    void writeBuffer(ByteBuffer src) throws IOException;
}
