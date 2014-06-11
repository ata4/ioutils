/*
 ** 2014 June 08
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket.provider;

import info.ata4.io.channels.CloseShieldReadableByteChannel;
import info.ata4.io.socket.IOSocket;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ReadableByteChannelProvider extends SocketProvider<ReadableByteChannel> {
    
    public ReadableByteChannelProvider(IOSocket socket) {
        super(socket);
    }

    @Override
    public ReadableByteChannel create() {
        InputStream stream = socket.getInputStream();
        if (stream != null) {
            return Channels.newChannel(stream);
        } else {
            return null;
        }
    }

    @Override
    protected ReadableByteChannel getCloseShield(ReadableByteChannel channel) {
        return new CloseShieldReadableByteChannel(channel);
    }
}
