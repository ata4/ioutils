/*
 ** 2014 February 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class StreamSocket extends IOSocket {

    public StreamSocket(InputStream is) {
        setInputStream(is);
        setCanRead(true);
    }
    
    public StreamSocket(OutputStream os) {
        setOutputStream(os);
        setCanWrite(true);
    }
}
