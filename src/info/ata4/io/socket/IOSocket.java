/*
 ** 2014 Februar 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.InverseDataInputStream;
import info.ata4.io.InverseDataOutputStream;
import info.ata4.io.Seekable;
import info.ata4.io.Swappable;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import org.apache.commons.io.input.CloseShieldInputStream;
import org.apache.commons.io.output.CloseShieldOutputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class IOSocket implements Closeable {
    
    private InputStream is;
    private OutputStream os;
    private DataInput in;
    private DataOutput out;
    private ReadableByteChannel rchan;
    private WritableByteChannel wchan;
    private ByteBuffer buf;
    private Swappable swappable;
    private Seekable seekable;
    
    protected InputStream newInputStream() {
        ReadableByteChannel chan = getReadableByteChannel();
        if (chan != null) {
            return Channels.newInputStream(chan);
        }
        
        DataInput input = getDataInput();
        if (input != null) {
            return new InverseDataInputStream(input);
        }
        
        return null;
    }
    
    protected InputStream getCloseShield(InputStream is) {
        if (is == null) {
            return null;
        } else {
            return new CloseShieldInputStream(is);
        }
    }
    
    public InputStream getInputStream() {
        if (is == null) {
            is = newInputStream();
        }
        return getCloseShield(is);
    }

    public void setInputStream(InputStream is) {
        this.is = is;
    }
    
    protected OutputStream newOutputStream() {
        WritableByteChannel chan = getWritableByteChannel();
        if (chan != null) {
            return Channels.newOutputStream(chan);
        }
        
        DataOutput output = getDataOutput();
        if (output != null) {
            return new InverseDataOutputStream(output);
        }
    
        return null;
    }
    
    protected OutputStream getCloseShield(OutputStream os) {
        if (os == null) {
            return null;
        } else {
            return new CloseShieldOutputStream(os);
        }
    }
    
    public OutputStream getOutputStream() {
        if (os == null) {
            os = newOutputStream();
        }
        return getCloseShield(os);
    }
    
    public void setOutputStream(OutputStream os) {
        this.os = os;
    }
    
    protected DataInput newDataInput() {
        InputStream stream = getInputStream();
        if (stream != null) {
            return new DataInputStream(stream);
        } else {
            return null;
        }
    }
    
    public DataInput getDataInput() {
        if (in == null) {
            in = newDataInput();
        }
        return in;
    }
    
    public void setDataInput(DataInput in) {
        this.in = in;
    }
    
    protected DataOutput newDataOutput() {
        OutputStream stream = getOutputStream();
        if (stream != null) {
            return new DataOutputStream(stream);
        } else {
            return null;
        }
    }
    
    public DataOutput getDataOutput() {
        if (out == null) {
            out = newDataOutput();
        }
        return out;
    }
    
    public void setDataOutput(DataOutput out) {
        this.out = out;
    }
    
    protected ReadableByteChannel newReadableByteChannel() {
        InputStream stream = getInputStream();
        if (stream != null) {
            return Channels.newChannel(stream);
        } else {
            return null;
        }
    }
    
    protected ReadableByteChannel getCloseShield(ReadableByteChannel chan) {
        if (chan == null) {
            return null;
        } else {
            return new CloseShieldReadableByteChannel(chan);
        }
    }
    
    public ReadableByteChannel getReadableByteChannel() {
        if (rchan == null) {
            rchan = newReadableByteChannel();
        }
        return getCloseShield(rchan);
    }
    
    public void setReadableByteChannel(ReadableByteChannel rchan) {
        this.rchan = rchan;
    }
    
    protected WritableByteChannel newWritableByteChannel() {
        OutputStream stream = getOutputStream();
        if (stream != null) {
            return Channels.newChannel(stream);
        } else {
            return null;
        }
    }
    
    protected WritableByteChannel getCloseShield(WritableByteChannel chan) {
        if (chan == null) {
            return null;
        } else {
            return new CloseShieldWritableByteChannel(chan);
        }
    }
    
    public WritableByteChannel getWritableByteChannel() {
        if (wchan == null) {
            wchan = newWritableByteChannel();
        }
        return getCloseShield(wchan);
    }
    
    public void setWritableByteChannel(WritableByteChannel wchan) {
        this.wchan = wchan;
    }
    
    protected ByteBuffer newByteBuffer() {
        return null;
    }
    
    public ByteBuffer getByteBuffer() {
        if (buf == null) {
            buf = newByteBuffer();
        }
        return buf;
    }
    
    public void setByteBuffer(ByteBuffer buf) {
        this.buf = buf;
    }
    
    protected Swappable newSwappable() {
        return null;
    }
    
    public Swappable getSwappable() {
        if (swappable == null) {
            swappable = newSwappable();
        }
        return swappable;
    }

    protected Seekable newSeekable() {
        return null;
    }

    public Seekable getSeekable() {
        if (seekable == null) {
            seekable = newSeekable();
        }
        return seekable;
    }

    @Override
    public void close() throws IOException {
        close(is);
        close(os);
        close(rchan);
        close(wchan);
    }
    
    protected void close(Closeable c) throws IOException {
        if (c != null) {
            c.close();
        }
    }
    
    private class CloseShieldChannel implements Channel {
        
        private boolean open;

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void close() throws IOException {
            open = false;
        }
    }
    
    private class CloseShieldReadableByteChannel extends CloseShieldChannel implements ReadableByteChannel {
        
        private final ReadableByteChannel chan;

        private CloseShieldReadableByteChannel(ReadableByteChannel chan) {
            this.chan = chan;
        }

        @Override
        public int read(ByteBuffer dst) throws IOException {
            if (!isOpen()) {
                throw new ClosedChannelException();
            }
            return chan.read(dst);
        }
    }
    
    private class CloseShieldWritableByteChannel extends CloseShieldChannel implements WritableByteChannel {
        
        private final WritableByteChannel chan;

        private CloseShieldWritableByteChannel(WritableByteChannel chan) {
            this.chan = chan;
        }

        @Override
        public int write(ByteBuffer src) throws IOException {
            if (!isOpen()) {
                throw new ClosedChannelException();
            }
            return chan.write(src);
        }
    }
}
