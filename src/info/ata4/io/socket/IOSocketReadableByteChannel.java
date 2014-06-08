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

import info.ata4.io.channels.CloseShieldReadableByteChannel;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
class IOSocketReadableByteChannel extends IOSocketChannel<ReadableByteChannel> {
    
    IOSocketReadableByteChannel(IOSocket socket) {
        super(socket);
    }

    @Override
    ReadableByteChannel newChannel() {
        InputStream stream = socket.getRawInputStream();
        if (stream != null) {
            return Channels.newChannel(stream);
        } else {
            return null;
        }
    }

    @Override
    ReadableByteChannel newCloseShieldChannel(ReadableByteChannel channel) {
        return new CloseShieldReadableByteChannel(channel);
    }
}
