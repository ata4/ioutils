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

import info.ata4.io.streams.InverseDataInputStream;
import info.ata4.io.socket.IOSocket;
import java.io.DataInput;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.io.input.CloseShieldInputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class InputStreamProvider extends SocketProvider<InputStream> {
    
    public InputStreamProvider(IOSocket socket) {
        super(socket);
    }
    
    @Override
    protected InputStream create() {
        ReadableByteChannel chan = socket.getReadableByteChannel();
        if (chan != null) {
            return Channels.newInputStream(chan);
        }

        DataInput input = socket.getDataInput();
        if (input != null) {
            return new InverseDataInputStream(input);
        }

        return null;
    }

    @Override
    protected InputStream getCloseShield(InputStream instance) {
        return new CloseShieldInputStream(instance);
    }
}
