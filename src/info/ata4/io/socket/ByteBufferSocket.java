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

import info.ata4.io.SeekableImpl;
import info.ata4.io.buffer.ByteBufferDataInput;
import info.ata4.io.buffer.ByteBufferDataOutput;
import info.ata4.io.buffer.ByteBufferInputStream;
import info.ata4.io.buffer.ByteBufferOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Wrapper for ByteBuffers to implement various IO interfaces.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferSocket extends IOSocket {
    
    private final ByteBuffer buf;
    
    public ByteBufferSocket(ByteBuffer buf) {
        this.buf = buf;
        
        setCanRead(true);
        setCanWrite(!buf.isReadOnly());
        setSeekable(new ByteBufferSeekable(buf));
        
        getDataInputProvider().set(new ByteBufferDataInput(buf));
        getDataOutputProvider().set(new ByteBufferDataOutput(buf));
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return buf;
    }

    @Override
    public ByteBufferInputStream getInputStream() {
        return new ByteBufferInputStream(buf);
    }

    @Override
    public ByteBufferOutputStream getOutputStream() {
        return new ByteBufferOutputStream(buf);
    }
    
    private class ByteBufferSeekable extends SeekableImpl {
        
        private final ByteBuffer buf;
        
        private ByteBufferSeekable(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public void position(long pos) throws IOException {
            if (pos > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Pointer is too large (> " + Integer.MAX_VALUE + ")");
            }
            buf.position((int) pos);
        }

        @Override
        public long position() throws IOException {
            return buf.position();
        }

        @Override
        public long capacity() throws IOException {
            return buf.capacity();
        }

        @Override
        public boolean hasRemaining() throws IOException {
            return buf.hasRemaining();
        }

        @Override
        public long remaining() throws IOException {
            return buf.remaining();
        }
    }
}
