/*
 ** 2014 February 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.data;

import info.ata4.io.socket.IOSocket;
import info.ata4.io.socket.MutableIOSocketProperties;
import java.io.DataInput;
import java.io.DataOutput;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataSocket extends IOSocket {
    
    public DataSocket(DataInput in) {
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setStreaming(true);
        props.setReadable(true);
        setProperties(props);
        
        getDataInputProvider().set(in);
    }
    
    public DataSocket(DataOutput out) {
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setStreaming(true);
        props.setWritable(true);
        props.setGrowable(true);
        setProperties(props);
        
        getDataOutputProvider().set(out);
    }
}
