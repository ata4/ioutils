/*
 ** 2015 March 07
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer.source;

import info.ata4.log.LogUtils;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class WritableByteChannelSource extends ChannelSource<WritableByteChannel> {
    
    private static final Logger L = LogUtils.getLogger();

    public WritableByteChannelSource(ByteBuffer buf, WritableByteChannel chan) {
        super(buf, chan);
    }
    
    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public boolean canGrow() {
        return true;
    }

    @Override
    public boolean canSeek() {
        return false;
    }
    
    @Override
    public void flush() throws IOException {        
        // stop here and start from the beginning
        buf.flip();
        
        int start = buf.position();
        
        // write buffer to channel
        while (chan.write(buf) > 0);
        
        L.log(Level.FINEST, "flush: {0} bytes written", buf.position() - start);
        
        buf.clear();
    }
    
    @Override
    public int read(ByteBuffer dst) throws IOException {
        throw new NonReadableSourceException();
    }
    
    @Override
    public int write(ByteBuffer src) throws IOException {
        int n = chanBuf.write(src);
        
        // check if buffer is empty
        if (n == -1) {
            L.finest("write: buffer full");
            
            flush();
            
            if (src.remaining() > buf.capacity()) {
                L.finest("write: write buffer directly");
                
                // src buffer larger than internal buffer, write directly
                n = chan.write(src);
            } else {
                // write buffered
                n = chanBuf.write(src);
            }
        }
        
        return n;
    }
    
    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {
        throw new NonReadableSourceException();
    }
    
    @Override
    public ByteBuffer requestWrite(int required) throws EOFException, IOException {
        // check if additional bytes need to be buffered
        if (buf.remaining() < required) {
            flush();

            // if there are still not enough bytes available, throw exception
            if (buf.remaining() < required) {
                throw new EOFException();
            }
        }
        
        return buf;
    }
}
