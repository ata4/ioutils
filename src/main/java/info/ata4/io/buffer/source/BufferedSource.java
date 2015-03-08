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

import info.ata4.io.Positionable;
import info.ata4.io.Swappable;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface BufferedSource extends Positionable, Swappable, Closeable {
    
    /**
     * Returns the size of the buffer.
     * 
     * @return buffer size in bytes
     */
    public int bufferSize();
    
    /**
     * Checks whether this source is readable or not. If the source is not readable,
     * {@link #read} and {@link #requestRead} will throw NonReadableSourceException.
     * 
     * @return true if the source is readable.
     */
    public boolean canRead();
    
    /**
     * Checks whether this source is writable or not. If the source is not writable,
     * {@link #write} and {@link #requestWrite} will throw NonWritableSourceException.
     * 
     * @return true if the source is writable.
     */
    public boolean canWrite();
    
    /**
     * Checks whether this source can be written beyond the reported size. If the
     * source cannot grow, it has a fixed size and out of bound writes result in
     * EOFExceptions.
     * 
     * Always returns false if {@link #canWrite} returns false.
     * 
     * @return true if the size of the source can grow.
     */
    public boolean canGrow();
    
    /**
     * Checks whether this source provides freely positionable random access.
     * If the source is not seekable, methods like {@link position} or
     * {@link size} will throw NonSeekableSourceException.
     * 
     * @return true if the source is seekable
     */
    public boolean canSeek();
    
    /**
     * Flushes all pending bytes from the buffer to the data source, similar
     * to {@link java.io.OutputStream#flush()}.
     * 
     * Has no effect if the buffer source is not writable. In fact, it may have
     * no effect at all for some implementations.
     * 
     * @throws IOException if writing the pending data causes an error
     */
    public void flush() throws IOException;
    
    /**
     * Reads contents of this source to the buffer. Behaves like
     * {@link java.nio.channels.ReadableByteChannel#read(java.nio.ByteBuffer)}.
     * 
     * @param dst destination buffer
     * @return number of bytes read
     * @throws IOException if there was an reading error
     * @throws NonReadableSourceException if the source is not readable
     */
    public int read(ByteBuffer dst) throws IOException;
    
    /**
     * Writes contents from the buffer to this source. Behaves like
     * {@link java.nio.channels.WritableByteChannel#write(java.nio.ByteBuffer)}.
     * 
     * @param src source buffer
     * @return number of bytes written
     * @throws IOException if there was a writing error
     * @throws NonWritableSourceException if the source is not writable
     */
    public int write(ByteBuffer src) throws IOException;
    
    /**
     * Prepares the buffer for reading. The returned buffer is guaranteed to have
     * at least {@code required} remaining bytes, otherwise an EOFException is
     * thrown. Any changes done to the buffer are omitted if none of the writing
     * method is called.
     * 
     * @param required minimum number of bytes required
     * @return prepared byte buffer
     * @throws IOException if there was an reading error
     * @throws EOFException if the required number of bytes cannot be aquired
     * @throws NonReadableSourceException if the source is not readable
     */
    public ByteBuffer requestRead(int required) throws EOFException, IOException;
    
    /**
     * Prepares the buffer for reading and writing. The returned buffer is
     * guaranteed to have at least {@code required} remaining bytes, otherwise an
     * EOFException is thrown.
     * 
     * Unlike {@link #requestRead}, this method will write changes to the 
     * underlaying data source when required.
     * 
     * @param required minimum number of bytes required
     * @return prepared byte buffer
     * @throws IOException if there was a writing error
     * @throws EOFException if the required number of bytes cannot be aquired
     * @throws NonWritableSourceException if the source is not writable
     */
    public ByteBuffer requestWrite(int required) throws EOFException, IOException;
}
