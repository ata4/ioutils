/*
 ** 2014 June 11
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket.provider;

import info.ata4.io.socket.IOSocket;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.InputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataInputProvider extends SocketProvider<DataInput> {
    
    public DataInputProvider(IOSocket socket) {
        super(socket);
    }

    @Override
    protected DataInput create() {
        InputStream stream = socket.getInputStream();
        if (stream != null) {
            return new DataInputStream(stream);
        } else {
            return null;
        }
    }
    
}
