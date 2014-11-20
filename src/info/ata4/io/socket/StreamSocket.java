/*
 ** 2014 February 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.Positionable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class StreamSocket extends IOSocket {

    public StreamSocket(InputStream is) {
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setStreaming(true);
        props.setReadable(true);
        props.setBuffered(is instanceof BufferedInputStream);
        setProperties(props);
        
        CountingInputStream cis;
        
        if (is instanceof CountingInputStream) {
            cis = (CountingInputStream) is;
        } else {
            cis = new CountingInputStream(is);
            is = cis;
        }
        
        setPositionable(new InputStreamPositionable(cis));
        
        getInputStreamProvider().set(is);
    }
    
    public StreamSocket(OutputStream os) {
        MutableIOSocketProperties props = new MutableIOSocketProperties();
        props.setStreaming(true);
        props.setWritable(true);
        props.setGrowable(true);
        props.setBuffered(os instanceof BufferedOutputStream);
        setProperties(props);
        
        CountingOutputStream cos;
        
        if (os instanceof CountingOutputStream) {
            cos = (CountingOutputStream) os;
        } else {
            cos = new CountingOutputStream(os);
            os = cos;
        }
        
        setPositionable(new OutputStreamPositionable(cos));
        
        getOutputStreamProvider().set(os);
    }
    
    private class InputStreamPositionable implements Positionable {
        
        private final CountingInputStream is;

        private InputStreamPositionable(CountingInputStream is) {
            this.is = is;
        }

        @Override
        public void position(long where) throws IOException {
            long pos = position();
            if (where >= pos) {
                long left = where - pos;
                while (left > 0) {
                    left -= is.skip(left);
                }
            } else {
                // can't skip backward
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public long position() throws IOException {
            return is.getByteCount();
        }

        @Override
        public long size() throws IOException {
            return is.getByteCount();
        }

        @Override
        public long remaining() throws IOException {
            return is.available();
        }

        @Override
        public boolean hasRemaining() throws IOException {
            return remaining() > 0;
        }
        
    }
    
    private class OutputStreamPositionable implements Positionable {
        
        private final CountingOutputStream os;

        private OutputStreamPositionable(CountingOutputStream os) {
            this.os = os;
        }

        @Override
        public void position(long where) throws IOException {
            // doesn't work here
            throw new UnsupportedOperationException();
        }

        @Override
        public long position() throws IOException {
            return os.getByteCount();
        }

        @Override
        public long size() throws IOException {
            return Long.MAX_VALUE; // no defined end, so use max long value
        }

        @Override
        public long remaining() throws IOException {
            return size() - position();
        }

        @Override
        public boolean hasRemaining() throws IOException {
            return remaining() > 0;
        }
    }
}
