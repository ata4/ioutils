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

import info.ata4.io.streams.InverseDataOutputStream;
import info.ata4.io.socket.IOSocket;
import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.apache.commons.io.output.ClosedOutputStream;
import org.apache.commons.io.output.ProxyOutputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class OutputStreamProvider extends SocketProvider<OutputStream> {
    
    public OutputStreamProvider(IOSocket socket) {
        super(socket);
    }
    
    @Override
    protected OutputStream create() {
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

    @Override
    protected OutputStream getCloseShield(OutputStream instance) {
        return new CloseShieldOutputStream(instance);
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
