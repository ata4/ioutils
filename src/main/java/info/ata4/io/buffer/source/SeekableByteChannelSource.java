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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class SeekableByteChannelSource extends ByteChannelSource {
    
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

    public SeekableByteChannelSource(ByteBuffer buffer, SeekableByteChannel chan) {
        // find out whether the channel is actually readable and/or writable in a
        // non-destructive way by using an empty buffer
        super(buffer, checkRead(chan), checkWrite(chan));
        this.chan = chan;
    }
    
    @Override
    public void fill() throws IOException {
        if (!canRead()) {
            return;
        }
        bufPos = chan.position() - buf.remaining();
        super.fill();
    }
    
    @Override
    public void flush() throws IOException {
        if (!canWrite() || !isDirty()) {
            return;
        }
        chan.position(bufPos);
        super.flush();
    }

    @Override
    public long position() throws IOException {
        return bufPos + buf.position();
    }

    @Override
    public void position(long newPos) throws IOException {
        // check if the new position is outside the buffered range
        if (newPos < bufPos + buf.position() || newPos > bufPos + buf.limit()) {
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
