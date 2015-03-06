/*
 ** 2015 March 01
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
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class SeekableByteChannelSource extends ByteChannelSource {
    
    private static final Logger L = LogUtils.getLogger();
    
    private static ReadableByteChannel checkRead(ReadableByteChannel chan) {
        try {
            chan.read(ByteBuffer.allocate(0));
            return chan;
        } catch (NonReadableChannelException | IOException ex) {
            return null;
        }
    }
    
    private static WritableByteChannel checkWrite(WritableByteChannel chan) {
        try {
            chan.write(ByteBuffer.allocate(0));
            return chan;
        } catch (NonWritableChannelException | IOException ex) {
            return null;
        }
    }
    
    private final SeekableByteChannel chan;
    private long bufPos;
    private boolean bufEOF;

    public SeekableByteChannelSource(ByteBuffer buffer, SeekableByteChannel chan) {
        // find out whether the channel is actually readable and/or writable in a
        // non-destructive way by using an empty buffer
        super(buffer, checkRead(chan), checkWrite(chan));
        this.chan = chan;
    }
    
    @Override
    public boolean canSeek() {
        return true;
    }
    
    @Override
    public void fill() throws IOException {
        if (!canRead()) {
            return;
        }
        bufPos = chan.position() - buf.remaining();
        L.log(Level.FINEST, "fill: at pos {0}", bufPos);
        
        super.fill();
        
        // if the buffer is empty after being filled, it's an EOF buffer
        bufEOF = buf.limit() == 0;
    }
    
    @Override
    public void flush() throws IOException {
        if (!canWrite() || !isDirty()) {
            return;
        }
        
        L.log(Level.FINEST, "flush: at pos {0}", bufPos);
        chan.position(bufPos);
        
        super.flush();
        
        bufEOF = false;
    }
    
    @Override
    public long position() throws IOException {
        return bufPos + buf.position();
    }
    
    @Override
    public int read(ByteBuffer dst) throws IOException {
        // EOF buffers must not be read
        if (bufEOF) {
            throw new EOFException();
        }
        
        return super.read(dst);
    }
    
    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {
        // EOF buffers must not be read
        if (bufEOF) {
            throw new EOFException();
        }
        
        return super.requestRead(required);
    }

    @Override
    public void position(long newPos) throws IOException {
        L.log(Level.FINEST, "postion: {0}", newPos);
        
        // check if the new position is outside the buffered range
        if (newPos < bufPos + buf.position() || newPos > bufPos + buf.limit()) {
            L.finest("postion: outside buffer");
            
            flush();
            bufPos = newPos;
            chan.position(newPos);
            clear();
        }

        // use difference to bufPos as position for the buffer
        buf.position((int) (newPos - bufPos));
    }

    @Override
    public long size() throws IOException {
        return Math.max(chan.size(), position());
    }
}
