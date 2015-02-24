/*
 ** 2015 February 22
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channel;

import info.ata4.io.Positionable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class BufferedReadablePositionableByteChannel extends BufferedReadableByteChannel implements Positionable {

    private final SeekableByteChannel schan;
    private final long chanSize;
    private long pos;

    public BufferedReadablePositionableByteChannel(SeekableByteChannel in, int bufferSize) throws IOException {
        super(in, bufferSize);
        this.schan = in;
        this.chanSize = schan.size();
    }
    
    public BufferedReadablePositionableByteChannel(SeekableByteChannel in) throws IOException {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    @Override
    protected int readDirect(ByteBuffer dst) throws IOException {
        schan.position(pos);
        return super.readDirect(dst);
    }

    @Override
    public long position() throws IOException {
        return pos + buf.position();
    }

    @Override
    public void position(long newPos) throws IOException {
        // get position snapped to buffer size
        long bufSize = bufferSize();
        long snapPos = (newPos / bufSize) * bufSize;

        // refill buffer if empty or if channel position has changed
        if (snapPos != pos || buf.limit() == 0) {
            pos = snapPos;
            buf.limit(0);
            fill();
        }
        
        // update buffer position
        buf.position((int) (newPos - snapPos));
    }

    @Override
    public long size() throws IOException {
        return chanSize;
    }
}
