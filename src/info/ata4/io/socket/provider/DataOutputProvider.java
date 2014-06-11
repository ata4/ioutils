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
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataOutputProvider extends SocketProvider<DataOutput> {
    
    public DataOutputProvider(IOSocket socket) {
        super(socket);
    }
    
    @Override
    protected DataOutput create() {
        OutputStream stream = socket.getOutputStream();
        if (stream != null) {
            return new DataOutputStream(stream);
        } else {
            return null;
        }
    }
    
}
