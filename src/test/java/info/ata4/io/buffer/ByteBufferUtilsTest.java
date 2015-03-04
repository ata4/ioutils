/*
 ** 2015 March 04
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import java.nio.ByteBuffer;
import static org.junit.Assert.*;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferUtilsTest {
    
    private static ByteBuffer allocateTestBuffer(int capacity) {
        ByteBuffer bb = ByteBuffer.allocate(capacity);
        for (int i = 0; i < capacity; i++) {
            bb.put((byte) (i & 0xff));
        }
        bb.flip();
        return bb;
    }
    
    @org.junit.Test
    public void testTransferSameSize() {
        ByteBuffer src = allocateTestBuffer(100);
        ByteBuffer dst = ByteBuffer.allocate(100);
        
        ByteBufferUtils.transfer(src, dst);
        
        assertEquals(src.remaining(), 0);
        assertEquals(dst.remaining(), 0);
        
        assertEquals(src.limit(), src.capacity());
        assertEquals(dst.limit(), dst.capacity());
        
        src.rewind();
        dst.rewind();
        
        assertEquals(src.hashCode(), dst.hashCode());
    }
    
    @org.junit.Test
    public void testTransferSmallSrc() {
        ByteBuffer src = allocateTestBuffer(50);
        ByteBuffer dst = ByteBuffer.allocate(100);
        
        ByteBufferUtils.transfer(src, dst);
        
        assertEquals(src.remaining(), 0);
        assertEquals(dst.remaining(), 50);
        
        assertEquals(src.limit(), src.capacity());
        assertEquals(dst.limit(), dst.capacity());
        
        src.rewind();
        dst.flip();
        
        assertEquals(src.hashCode(), dst.hashCode());
    }
    
    @org.junit.Test
    public void testTransferSmallDst() {
        ByteBuffer src = allocateTestBuffer(100);
        ByteBuffer dst = ByteBuffer.allocate(50);
        
        ByteBufferUtils.transfer(src, dst);
        
        assertEquals(src.remaining(), 50);
        assertEquals(dst.remaining(), 0);
        
        assertEquals(src.limit(), src.capacity());
        assertEquals(dst.limit(), dst.capacity());
        
        src.flip();
        dst.rewind();
        
        assertEquals(src.hashCode(), dst.hashCode());
    }
    
    @org.junit.Test
    public void testTransferLimitedSrc() {
        int ofs = 25;
        int limit = 75;
        
        ByteBuffer src = allocateTestBuffer(100);
        ByteBuffer dst = ByteBuffer.allocate(limit - ofs);
        
        src.position(ofs);
        src.limit(limit);
        
        ByteBufferUtils.transfer(src, dst);
        
        assertEquals(src.remaining(), 0);
        assertEquals(dst.remaining(), 0);
        
        assertEquals(src.limit(), limit);
        assertEquals(dst.limit(), dst.capacity());
        
        src.position(ofs);
        src = src.slice();
        
        dst.rewind();
        
        assertEquals(src.hashCode(), dst.hashCode());
    }

    @org.junit.Test
    public void testNonEmpty() {
        ByteBuffer bb = ByteBuffer.allocate(1);
        assertFalse(ByteBufferUtils.isEmpty(bb));
    }
    
    @org.junit.Test
    public void testEmpty() {
        ByteBuffer bb = ByteBuffer.allocate(0);
        assertTrue(ByteBufferUtils.isEmpty(bb));
        assertTrue(ByteBufferUtils.isEmpty(null));
    }
}
