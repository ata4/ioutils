/*
 ** 2014 June 08
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class CloseShieldReadableByteChannel extends CloseShieldChannel<ReadableByteChannel> implements ReadableByteChannel {
    
    public CloseShieldReadableByteChannel(ReadableByteChannel channel) {
        super(channel);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
        return channel.read(dst);
    }
}
