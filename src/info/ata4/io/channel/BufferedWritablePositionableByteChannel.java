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
public class BufferedWritablePositionableByteChannel extends BufferedWritableByteChannel implements Positionable {
    
    private final SeekableByteChannel schan;
    private final long schanSize;
    private long pos;
    
    public BufferedWritablePositionableByteChannel(SeekableByteChannel in, int bufferSize) throws IOException {
        super(in, bufferSize);
        this.schan = in;
        this.schanSize = schan.size();
    }
    
    public BufferedWritablePositionableByteChannel(SeekableByteChannel in) throws IOException {
        this(in, DEFAULT_BUFFER_SIZE);
    }
    
    @Override
    public int writeDirect(ByteBuffer src) throws IOException {
        schan.position(pos);
        int written = super.writeDirect(src);
        pos += written;
        return written;
    }

    @Override
    public void position(long where) throws IOException {
        flush();
        pos = where;
        schan.position(pos);
    }

    @Override
    public long position() throws IOException {
        return pos + pending();
    }

    @Override
    public long size() throws IOException {
        return Math.max(schanSize, position());
    }
    
}
