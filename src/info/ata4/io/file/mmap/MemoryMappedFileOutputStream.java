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
import java.io.OutputStream;
import java.nio.BufferOverflowException;

/**
 * OutputStream wrapper for byte buffers.
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFileOutputStream extends OutputStream {

    private final MemoryMappedFile mmf;

    public MemoryMappedFileOutputStream(MemoryMappedFile mmf) {
        this.mmf = mmf;
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            mmf.put((byte) b);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized void write(byte[] bytes, int off, int len) throws IOException {
        try {
            mmf.put(bytes, off, len);
        } catch (BufferOverflowException ex) {
            throw new IOException(ex);
        }
    }
}
