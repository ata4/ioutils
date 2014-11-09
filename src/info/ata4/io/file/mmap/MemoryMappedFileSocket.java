/*
 ** 2014 September 12
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.file.mmap;

import info.ata4.io.Positionable;
import info.ata4.io.Swappable;
import info.ata4.io.socket.IOSocket;
import info.ata4.io.socket.MutableIOSocketProperties;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFileSocket extends IOSocket {
    
    private final MemoryMappedFile mmf;
    
    public MemoryMappedFileSocket(MemoryMappedFile mmf) {
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setReadable(true);
        props.setWritable(!mmf.isReadOnly());
        props.setBuffered(true);
        setProperties(props);
        
        getInputStreamProvider().set(new MemoryMappedFileInputStream(mmf));
        getOutputStreamProvider().set(new MemoryMappedFileOutputStream(mmf));
        
        getDataInputProvider().set(new MemoryMappedFileDataInput(mmf));
        getDataOutputProvider().set(new MemoryMappedFileDataOutput(mmf));
        
        setPositionable(new MemoryMappedFilePositionable(mmf));
        setSwappable(new MemoryMappedFileSwappable(mmf));
        
        this.mmf = mmf;
    }

    @Override
    public void close() throws IOException {
        super.close();
        mmf.close();
    }
    
    private class MemoryMappedFileSwappable implements Swappable {
        
        private final MemoryMappedFile mmf;
        
        private MemoryMappedFileSwappable(MemoryMappedFile mmf) {
            this.mmf = mmf;
        }

        @Override
        public boolean isSwap() {
            return mmf.order() == ByteOrder.LITTLE_ENDIAN;
        }

        @Override
        public void setSwap(boolean swap) {
            mmf.order(swap ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        }
    }
    
    private class MemoryMappedFilePositionable implements Positionable {
        
        private final MemoryMappedFile mmf;
        
        private MemoryMappedFilePositionable(MemoryMappedFile mmf) {
            this.mmf = mmf;
        }

        @Override
        public void position(long where) throws IOException {
            mmf.setPosition(where);
        }

        @Override
        public long position() throws IOException {
            return mmf.getPosition();
        }

        @Override
        public long size() throws IOException {
            return mmf.getSize();
        }

        @Override
        public long remaining() throws IOException {
            return mmf.getRemaining();
        }

        @Override
        public boolean hasRemaining() throws IOException {
            return mmf.hasRemaining();
        }
        
    }
}
