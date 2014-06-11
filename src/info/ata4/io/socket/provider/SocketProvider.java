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

import info.ata4.io.socket.IOSocket;
import java.io.Closeable;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class SocketProvider<T> implements Closeable {
    
    protected final IOSocket socket;
    protected T shielded;
    protected T unshielded;
    
    public SocketProvider(IOSocket socket) {
        this.socket = socket;
    }
    
    protected T getCloseShield(T instance) {
        return instance;
    }
    
    protected abstract T create();
    
    public T get() {
        // create initial instance
        if (unshielded == null) {
            unshielded = create();
        }
        
        // stop here if there's no instance
        if (unshielded == null) {
            return unshielded;
        }
        
        // protect instance from closing
        shielded = getCloseShield(unshielded);
        
        return shielded;
    }
    
    public void set(T instance) {
        this.unshielded = instance;
    }
    
    protected void releaseShielded() {
        close(shielded);
        shielded = null;
    }
    
    protected void releaseUnshielded() {
        close(unshielded);
        unshielded = null;
    }
    
    protected void close(T o) {
        if (o == null) {
            return;
        }
        
        if (!(o instanceof Closeable)) {
            return;
        }
        
        Closeable c = (Closeable) o;
        IOUtils.closeQuietly(c);
    }
    
    @Override
    public void close() throws IOException {
        releaseUnshielded();
        releaseShielded();
    }
}
