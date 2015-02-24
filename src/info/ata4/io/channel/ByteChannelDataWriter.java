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

import info.ata4.io.DataWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * OutputStream/DataOutput/WritableByteChannel implementation that wraps
 * a WritableByteChannel and uses a ByteBuffer for buffering.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteChannelDataWriter extends DataWriter implements Channel {
    
    protected final BufferedWritableByteChannel chan;
    protected final OutputStream os;

    public ByteChannelDataWriter(BufferedWritableByteChannel out) {        
        chan = out;
        os = Channels.newOutputStream(chan);
    }
    
    public ByteChannelDataWriter(WritableByteChannel out) {
        this(new BufferedWritableByteChannel(out));
    }
    
    public ByteChannelDataWriter(WritableByteChannel out, int bufferSize) {
        this(new BufferedWritableByteChannel(out, bufferSize));
    }
    
    @Override
    public OutputStream stream() {
        return os;
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
    
    ////////////////
    // DataOutput //
    ////////////////
    
    @Override
    public void writeBytes(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        ByteBuffer buf = chan.buffer();
        if (buf.remaining() >= len) {
            buf.put(b, off, len);
        } else {
            chan.write(ByteBuffer.wrap(b, off, len));
        }
    }
    
    @Override
    public void writeBuffer(ByteBuffer buf) throws IOException {
        chan.write(buf);
    }

    @Override
    public void writeByte(byte b) throws IOException {
        chan.buffer(1).put(b);
    }
    
    @Override
    public void writeBoolean(boolean v) throws IOException {
        chan.buffer(1).put((byte) (v ? 1 : 0));
    }

    @Override
    public void writeShort(short v) throws IOException {
        chan.buffer(2).putShort(v);
    }

    @Override
    public void writeChar(char v) throws IOException {
        chan.buffer(2).putChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        chan.buffer(4).putInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        chan.buffer(8).putLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        chan.buffer(4).putFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        chan.buffer(8).putDouble(v);
    }
}
