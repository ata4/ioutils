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

import info.ata4.io.InverseDataOutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ClosedOutputStream;
import org.apache.commons.io.output.ProxyOutputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
class IOSocketOutputStream extends IOSocketProvider {
    
    private OutputStream raw;
    private OutputStream active;
    
    IOSocketOutputStream(IOSocket socket) {
        super(socket);
    }
    
    private OutputStream newOutputStream() {
        WritableByteChannel chan = socket.getWritableByteChannel();
        if (chan != null) {
            return Channels.newOutputStream(chan);
        }

        DataOutput output = socket.getDataOutput();
        if (output != null) {
            return new InverseDataOutputStream(output);
        }

        return null;
    }
    
    OutputStream getRawOutputStream() {
        if (raw == null) {
            raw = newOutputStream();
        }
        return raw;
    }

    void setRawOutputStream(OutputStream os) {
        this.raw = os;
    }
    
    OutputStream getOutputStream() {
        // close previously created stream
        IOUtils.closeQuietly(active);
        
        // get main input stream
        active = getRawOutputStream();
        
        // stop here if there's no stream
        if (active == null) {
            return active;
        }

        // protect actual stream from closing
        active = new CloseShieldOutputStream(active);
        
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
    
    // custom CloseShieldOutputStream implementation that also flushes any
    // buffered data in the underlaying OutputStream when "closed"
    private class CloseShieldOutputStream extends ProxyOutputStream {

        private CloseShieldOutputStream(OutputStream out) {
            super(out);
        }

        @Override
        public void close() throws IOException {
            out.flush();
            out = new ClosedOutputStream();
        }
    }
}
