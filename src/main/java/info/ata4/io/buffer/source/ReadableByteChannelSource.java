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
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ReadableByteChannelSource extends ChannelSource<ReadableByteChannel> {
    
    private static final Logger L = LogUtils.getLogger();
    
    public ReadableByteChannelSource(ByteBuffer buf, ReadableByteChannel chan) {
        super(buf, chan);
    }

    @Override
    public int bufferSize() {
        return buf.capacity();
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public boolean canGrow() {
        return false;
    }

    @Override
    public boolean canSeek() {
        return false;
    }
    
    public void fill() throws IOException {
        // copy remaining bytes to the beginning of the buffer
        buf.compact();
        
        // clear limit
        buf.limit(buf.capacity());
        
        int start = buf.position();

        // fill buffer from channel
        while (chan.read(buf) > 0);
        
        L.log(Level.FINEST, "fill: {0} bytes read", buf.position() - start);

        // start from the beginning
        buf.flip();
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int n = chanBuf.read(dst);
        
        // check if buffer is empty
        if (n == -1) {
            L.finest("read: buffer empty");

            if (dst.remaining() > buf.capacity()) {
                // dst buffer larger than internal buffer, read directly
                L.finest("read: read buffer directly");
                n = chan.read(dst);
            } else {
                // fill buffer and then read buffered
                fill();
                n = chanBuf.read(dst);
            }
        }
        return n;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        throw new NonWritableSourceException();
    }

    @Override
    public ByteBuffer requestRead(int required) throws EOFException, IOException {        
        // check if additional bytes need to be buffered
        if (buf.remaining() < required) {
            L.log(Level.FINEST, "requestRead: need {0} more bytes", required - buf.remaining());
            fill();

            // if there are still not enough bytes available, throw exception
            if (buf.remaining() < required) {
                throw new EOFException();
            }
        }
        
        return buf;
    }

    @Override
    public ByteBuffer requestWrite(int required) throws EOFException, IOException {
        throw new NonWritableSourceException();
    }
    
    @Override
    public void close() throws IOException {
        chan.close();
    }
}
