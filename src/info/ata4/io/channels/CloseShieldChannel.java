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
import java.nio.channels.Channel;

/**
 *
 * @param <T>
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class CloseShieldChannel<T extends Channel> implements Channel {
    
    protected final T channel;
    private boolean open = true;
    
    public CloseShieldChannel(T channel) {
        this.channel = channel;
    }

    @Override
    public boolean isOpen() {
        return open && channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        open = false;
    }
    
}
