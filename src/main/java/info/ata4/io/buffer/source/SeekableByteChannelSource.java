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
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class SeekableByteChannelSource extends ChannelSource<SeekableByteChannel> {
    
    private static final Logger L = LogUtils.getLogger();
    
    private final ReadableByteChannelSource bufIn;
    private final WritableByteChannelSource bufOut;

    private boolean write;

    public SeekableByteChannelSource(ByteBuffer buffer, SeekableByteChannel chan) {
        super(buffer, chan);
        
        // find out whether the channel is actually readable and/or writable in a
        // non-destructive way by using an empty buffer
        ReadableByteChannelSource bufInTmp = null;
        try {
            chan.read(ByteBuffer.allocate(0));
            bufInTmp = new ReadableByteChannelSource(buffer, chan);
        } catch (NonReadableChannelException | IOException ex) {}
        bufIn = bufInTmp;
        
        WritableByteChannelSource bufOutTmp = null;
        try {
            chan.write(ByteBuffer.allocate(0));
            bufOutTmp = new WritableByteChannelSource(buffer, chan);
        } catch (NonWritableChannelException | IOException ex) {}
        bufOut = bufOutTmp;
        
        // switch to write mode if there's no readable channel
        write = bufIn == null;
    }
    
    private void setRead() throws IOException {
        if (!canRead()) {
            throw new NonReadableSourceException();
        }
        
        if (write) {
            L.finest("setRead");
            
            // write pending bytes
            flush();
            
            // clear write buffer
            clear();
            
            write = false;
        }
    }
    
    private void setWrite() throws IOException {
        if (!canWrite()) {
            throw new NonWritableSourceException();
        }
        
        if (!write) {
            L.finest("setWrite");
            
            // correct channel position by the number of unread bytes
            if (buf.hasRemaining()) {
                chan.position(chan.position() - buf.remaining());
            }
            
            // clear read buffer
            clear();
            
            write = true;
        }
    }
    
    public void clear() {
        L.finest("clear");
        
        // mark buffer as empty
        buf.limit(0);
    }
    
    @Override
    public boolean canRead() {
        return bufIn != null;
    }
    
    @Override
    public boolean canWrite() {
        return bufOut != null;
    }
    
    @Override
    public boolean canSeek() {
        return true;
    }
    
    @Override
    public boolean canGrow() {
        return canWrite() && bufOut.canGrow();
    }
    
    @Override
    public void flush() throws IOException {
        if (canWrite() && write) {
            bufOut.flush();
        }
    }
    
    @Override
    public long position() throws IOException {
        return write ? chan.position() + buf.position() : chan.position() - buf.remaining();
    }
    
    @Override
    public int read(ByteBuffer dst) throws IOException {
        setRead();
        return bufIn.read(dst);
    }
    
    @Override
    public int write(ByteBuffer src) throws IOException {
        setWrite();
        return bufOut.write(src);
    }
    
    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {
        setRead();
        return bufIn.requestRead(required);
    }
    
    @Override
    public ByteBuffer requestWrite(int required) throws EOFException, IOException {
        setWrite();
        return bufOut.requestWrite(required);
    }

    @Override
    public void position(long newPos) throws IOException {
        L.log(Level.FINEST, "postion: {0}", newPos);
        
        long pos = chan.position();
        boolean clear = false;
        
        if (write) {
            // in write mode, the buffer always needs to be cleared
            clear = true;
        } else {
            // in read mode, the buffer position may be moved forward
            if (newPos < pos + buf.position() || newPos > pos + buf.limit()) {
                L.finest("postion: outside read buffer");
                clear = true;
            }
        }
        
        if (clear) {
            flush();
            pos = newPos;
            chan.position(pos);
            clear();
        }

        // use difference between newPos and bufPos as position for the buffer
        buf.position((int) (newPos - pos));
    }

    @Override
    public long size() throws IOException {
        return Math.max(chan.size(), position());
    }
    
    public void truncate(long size) throws IOException {
        flush();
        clear();
        chan.truncate(size);
    }
}
