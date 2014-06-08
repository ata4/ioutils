/*
 ** 2014 June 08
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import java.io.Closeable;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class IOSocketProvider implements Closeable {
    
    protected final IOSocket socket;

    IOSocketProvider(IOSocket socket) {
        this.socket = socket;
    }
}
