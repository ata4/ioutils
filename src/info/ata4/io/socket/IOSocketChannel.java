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

import java.io.IOException;
import java.nio.channels.Channel;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
abstract class IOSocketChannel<T extends Channel> extends IOSocketProvider {
    
    private T raw;
    private T active;
    
    IOSocketChannel(IOSocket socket) {
        super(socket);
    }

    abstract T newChannel();
    abstract T newCloseShieldChannel(T channel);
    
    T getRawChannel() {
        if (raw == null) {
            raw = newChannel();
        }
        
        return raw;
    }
    
    void setRawChannel(T wchan) {
        this.raw = wchan;
    }

    T getChannel() {
        // close previously created channel
        IOUtils.closeQuietly(active);
        
        // get main input channel
        active = getRawChannel();
        
        // stop here if there's no channel
        if (active == null) {
            return active;
        }
        
        // protect actual channel from closing
        active = newCloseShieldChannel(active);
        
        return active;
    }
    
    @Override
    public void close() throws IOException {
        if (active != null) {
            active.close();
        }
        if (raw != null) {
            raw.close();
        }
    }
}
