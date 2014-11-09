/*
 ** 2013 June 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import info.ata4.io.ByteBufferReadable;
import info.ata4.io.ByteBufferWritable;
import info.ata4.io.Positionable;
import info.ata4.io.Swappable;
import info.ata4.io.socket.IOSocket;
import info.ata4.io.socket.MutableIOSocketProperties;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Wrapper for ByteBuffers to implement various IO interfaces.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferSocket extends IOSocket {
    
    private final ByteBuffer buf;
    
    public ByteBufferSocket(ByteBuffer buf) {
        this.buf = buf;
        
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setReadable(true);
        props.setWritable(!buf.isReadOnly());
        props.setBuffered(true);
        setProperties(props);
        
        setPositionable(new ByteBufferPositionable(buf));
        setSwappable(new ByteBufferSwappable(buf));
        setByteBufferReadable(new ByteBufferReader(buf));
        setByteBufferWritable(new ByteBufferWriter(buf));
        
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
    
    private class ByteBufferPositionable implements Positionable {
        
        private final ByteBuffer buf;
        
        private ByteBufferPositionable(ByteBuffer buf) {
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
        public long size() throws IOException {
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
    
    private class ByteBufferSwappable implements Swappable {
        
        private final ByteBuffer buf;
        
        private ByteBufferSwappable(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public boolean isSwap() {
            return buf.order() == ByteOrder.LITTLE_ENDIAN;
        }

        @Override
        public void setSwap(boolean swap) {
            buf.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
    }
    
    private class ByteBufferReader implements ByteBufferReadable {
        
        private final ByteBuffer buf;
        
        private ByteBufferReader(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public void readBuffer(ByteBuffer dst) throws IOException {
            try {
                while (dst.hasRemaining()) {
                    dst.put(buf.get());
                }
            } catch (BufferUnderflowException ex) {
                throw new IOException(ex);
            }
        }
    }
    
    private class ByteBufferWriter implements ByteBufferWritable {
        
        private final ByteBuffer buf;
        
        private ByteBufferWriter(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        public void writeBuffer(ByteBuffer src) throws IOException {
            try {
                buf.put(src);
            } catch (BufferOverflowException ex) {
                throw new IOException(ex);
            }
        }
    }
}
