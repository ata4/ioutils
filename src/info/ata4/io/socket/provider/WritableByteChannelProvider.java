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

import info.ata4.io.channels.CloseShieldWritableByteChannel;
import info.ata4.io.socket.IOSocket;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class WritableByteChannelProvider extends SocketProvider<WritableByteChannel> {
    
    public WritableByteChannelProvider(IOSocket socket) {
        super(socket);
    }

    @Override
    public WritableByteChannel create() {
        OutputStream stream = socket.getOutputStream();
        if (stream != null) {
            return Channels.newChannel(stream);
        } else {
            return null;
        }
    }

    @Override
    protected WritableByteChannel getCloseShield(WritableByteChannel channel) {
        return new CloseShieldWritableByteChannel(channel);
    }
}
