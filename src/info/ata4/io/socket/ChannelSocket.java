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

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ChannelSocket extends IOSocket {
    
    public ChannelSocket(ReadableByteChannel chan) {
        setReadableByteChannel(chan);
        setCanRead(true);
    }
    
    public ChannelSocket(WritableByteChannel chan) {
        setWritableByteChannel(chan);
        setCanWrite(true);
    }
}
