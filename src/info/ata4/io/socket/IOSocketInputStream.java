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

import info.ata4.io.InverseDataInputStream;
import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CloseShieldInputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
class IOSocketInputStream extends IOSocketProvider {
    
    private InputStream raw;
    private InputStream active;

    IOSocketInputStream(IOSocket socket) {
        super(socket);
    }
    
    private InputStream newInputStream() {
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
    
    InputStream getRawInputStream() {
        if (raw == null) {
            raw = newInputStream();
        }
        return raw;
    }
    
    void setRawInputStream(InputStream is) {
        this.raw = is;
    }
    
    InputStream getInputStream() {
        // close previously created stream
        IOUtils.closeQuietly(active);
        
        // get main input stream
        active = getRawInputStream();
        
        // stop here if there's no stream
        if (active == null) {
            return active;
        }
        
        // buffer stream if enabled
        if (socket.hasStreamBuffering()) {
            active = new BufferedInputStream(active);
        }
        
        // protect actual stream from closing
        active = new CloseShieldInputStream(active);
        
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
