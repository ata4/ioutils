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

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;

/**
 * InputStream wrapper for byte buffers.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFileInputStream extends InputStream {

    private final MemoryMappedFile mmf;

    public MemoryMappedFileInputStream(MemoryMappedFile mmf) {
        this.mmf = mmf;
    }
    
    public MemoryMappedFile getMemoryMappedFile() {
        return mmf;
    }
    
    @Override
    public int available() throws IOException {
        return (int) Math.min(Integer.MAX_VALUE, mmf.getSize() - mmf.getPosition());
    }

    @Override
    public synchronized int read() throws IOException {
        if (!mmf.hasRemaining()) {
            return -1;
        }

        try {
            return 0xff & mmf.get();
        } catch (BufferUnderflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
        if (!mmf.hasRemaining()) {
            return -1;
        }

        len = Math.min(len, available());

        try {
            mmf.get(bytes, off, len);
        } catch (BufferUnderflowException ex) {
            throw new IOException(ex);
        }
        
        return len;
    }
}
