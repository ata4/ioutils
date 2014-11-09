/*
 ** 2014 November 09
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MutableIOSocketProperties implements IOSocketProperties {
    
    private boolean readable;
    private boolean writable;
    private boolean growable;
    private boolean streaming;
    private boolean buffered;

    @Override
    public boolean isReadable() {
        return readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    @Override
    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    @Override
    public boolean isGrowable() {
        return growable;
    }

    public void setGrowable(boolean growable) {
        this.growable = growable;
    }

    @Override
    public boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(boolean streaming) {
        this.streaming = streaming;
    }

    public boolean isBuffered() {
        return buffered;
    }

    public void setBuffered(boolean buffered) {
        this.buffered = buffered;
    }

}
