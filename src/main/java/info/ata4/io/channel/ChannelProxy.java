/*
 ** 2015 February 22
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channel;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ChannelProxy<T extends Channel> implements Channel {
    
    protected final T chan;
    
    public ChannelProxy(T chan) {
        this.chan = chan;
    }

    @Override
    public boolean isOpen() {
        return chan.isOpen();
    }

    @Override
    public void close() throws IOException {
        chan.close();
    }
}
