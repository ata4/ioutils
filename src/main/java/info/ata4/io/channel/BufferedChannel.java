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

import info.ata4.io.buffer.source.BufferedSource;
import java.io.IOException;
import java.nio.channels.Channel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 * @param <C>
 * @param <B>
 */
public abstract class BufferedChannel <C extends Channel, B extends BufferedSource> implements Channel {
    
    protected final C chan;
    protected final B buf;
    
    public BufferedChannel(C chan, B buf) {
        this.chan = chan;
        this.buf = buf;
    }

    @Override
    public boolean isOpen() {
        return chan.isOpen();
    }

    @Override
    public void close() throws IOException {
        buf.close();
        chan.close();
    }
}
