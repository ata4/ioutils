/*
 ** 2015 January 24
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.stream;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * ByteArrayOutputStream extension that can wrap its working array directly into
 * a ByteBuffer.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class WrapByteArrayOutputStream extends ByteArrayOutputStream {

    public WrapByteArrayOutputStream() {
        super();
    }

    public WrapByteArrayOutputStream(int size) {
        super(size);
    }
    
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(buf, 0, count);
    }
}
