/*
 ** 2015 January 11
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channel;

import info.ata4.io.DataReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * InputStream/DataInput/ReadableByteChannel implementation that wraps
 * a ReadableByteChannel and uses a ByteBuffer for buffering.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteChannelDataReader extends DataReader implements Channel {
    
    protected final BufferedReadableByteChannel chan;
    protected final InputStream is;

    public ByteChannelDataReader(BufferedReadableByteChannel in) {
        chan = in;
        is = Channels.newInputStream(chan);
    }
    
    public ByteChannelDataReader(ReadableByteChannel in) {
        this(new BufferedReadableByteChannel(in));
    }
    
    public ByteChannelDataReader(ReadableByteChannel in, int bufferSize) {
        this(new BufferedReadableByteChannel(in, bufferSize));
    }
    
    @Override
    public InputStream stream() {
        return is;
    }
    
    ///////////////
    // Swappable //
    ///////////////
    
    @Override
    public ByteOrder order() {
        return chan.buffer().order();
    }
    
    @Override
    public void order(ByteOrder order) {
        chan.buffer().order(order);
    }
    
    /////////////
    // Channel //
    /////////////
    
    @Override
    public boolean isOpen() {
        return chan.isOpen();
    }
    
    @Override
    public void close() throws IOException {
        chan.close();
    }
    
    ///////////////
    // DataInput //
    ///////////////

    @Override
    public void readBytes(byte[] b, int off, int len) throws IOException {
        ByteBuffer buf = chan.buffer();
        if (buf.remaining() >= len) {
            buf.get(b, off, len);
        } else {
            readBuffer(ByteBuffer.wrap(b, off, len));
        }
    }
    
    @Override
    public void readBuffer(ByteBuffer buf) throws IOException {
        while (buf.hasRemaining()) {
            if (chan.read(buf) == -1) {
                throw new EOFException();
            }
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        return chan.buffer(1).get() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return chan.buffer(1).get();
    }

    @Override
    public short readShort() throws IOException {
        return chan.buffer(2).getShort();
    }

    @Override
    public char readChar() throws IOException {
        return chan.buffer(2).getChar();
    }

    @Override
    public int readInt() throws IOException {
        return chan.buffer(4).getInt();
    }

    @Override
    public long readLong() throws IOException {
        return chan.buffer(8).getLong();
    }

    @Override
    public float readFloat() throws IOException {
        return chan.buffer(4).getFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return chan.buffer(8).getDouble();
    }
}
