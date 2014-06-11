/*
 ** 2014 January 14
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.streams;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Counterpart of DataInputStream: reads from a DataInput as InputStream.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class InverseDataInputStream extends InputStream {
    
    private final DataInput in;

    public InverseDataInputStream(DataInput in) {
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        try {
            return in.readByte();
        } catch (EOFException ex) {
            return -1;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        try {
            in.readFully(b);
            return b.length;
        } catch (EOFException ex) {
            return -1;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        try {
            in.readFully(b, off, len);
            return len;
        } catch (EOFException ex) {
            return -1;
        }
    }
}
