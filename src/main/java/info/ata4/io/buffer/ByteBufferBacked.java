/*
 ** 2015 January 11
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import java.nio.ByteBuffer;

/**
 * Interface for classes that wrap a ByteBuffer.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface ByteBufferBacked {
    
    ByteBuffer buffer();
}
