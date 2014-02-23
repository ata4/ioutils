/*
 ** 2013 June 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.buffer.ByteBufferInput;
import info.ata4.io.buffer.ByteBufferInputStream;
import info.ata4.io.buffer.ByteBufferOutput;
import info.ata4.io.buffer.ByteBufferOutputStream;
import info.ata4.io.Seekable;
import info.ata4.io.SeekableImpl;
import info.ata4.io.Swappable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wrapper for ByteBuffers to implement various IO interfaces.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferSocket extends IOSocket {
    
    public ByteBufferSocket(ByteBuffer buf) {
        setByteBuffer(buf);
    }

    @Override
    protected InputStream newInputStream() {
        return new ByteBufferInputStream(getByteBuffer());
    }

    @Override
    protected OutputStream newOutputStream() {
        return new ByteBufferOutputStream(getByteBuffer());
    }

    @Override
    protected DataInput newDataInput() {
        return new ByteBufferInput(getByteBuffer());
    }

    @Override
    protected DataOutput newDataOutput() {
        return new ByteBufferOutput(getByteBuffer());
    }

    @Override
    protected Swappable newSwappable() {
        return new ByteBufferSwappable();
    }

    @Override
    protected Seekable newSeekable() {
        return new ByteBufferSeekable();
    }
    
    private class ByteBufferSwappable implements Swappable {

        @Override
        public boolean isSwap() {
            return getByteBuffer().order() != ByteOrder.BIG_ENDIAN;
        }

        @Override
        public void setSwap(boolean swap) {
            getByteBuffer().order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
    }
    
    private class ByteBufferSeekable extends SeekableImpl {

        @Override
        public void position(long pos) throws IOException {
            if (pos > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Pointer is too large (> " + Integer.MAX_VALUE + ")");
            }
            getByteBuffer().position((int) pos);
        }

        @Override
        public long position() throws IOException {
            return getByteBuffer().position();
        }

        @Override
        public long capacity() throws IOException {
            return getByteBuffer().capacity();
        }

        @Override
        public boolean hasRemaining() throws IOException {
            return getByteBuffer().hasRemaining();
        }

        @Override
        public long remaining() throws IOException {
            return getByteBuffer().remaining();
        }
    }
}
