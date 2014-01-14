/*
 ** 2014 January 14
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Counterpart of DataOutputStream: writes to a DataOutput as OutputStream.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class InverseDataOutputStream extends OutputStream {
    
    private final DataOutput out;

    public InverseDataOutputStream(DataOutput out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }
}
