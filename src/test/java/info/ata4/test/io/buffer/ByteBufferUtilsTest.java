/*
 ** 2015 March 04
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.test.io.buffer;

import info.ata4.io.buffer.ByteBufferUtils;
import java.nio.ByteBuffer;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferUtilsTest {
    
    private static ByteBuffer allocateTestBuffer(int capacity, boolean direct) {
        ByteBuffer bb;
        
        if (direct) {
            bb = ByteBuffer.allocateDirect(capacity);
        } else {
            bb = ByteBuffer.allocate(capacity);
        }
        
        for (int i = 0; i < capacity; i++) {
            bb.put((byte) (i & 0xff));
        }
        
        bb.flip();
        
        return bb;
    }
    
    private static ByteBuffer allocateTestBuffer(int capacity) {
        return allocateTestBuffer(capacity, false);
    }
    
    @Test
    public void testCopy() {
        ByteBuffer src = allocateTestBuffer(100);
        ByteBuffer dst = ByteBufferUtils.copy(src);
        
        assertEquals(src.hashCode(), dst.hashCode());
    }
    
    @Test
    public void testCopyDirect() {
        ByteBuffer src = allocateTestBuffer(100, true);
        ByteBuffer dst = ByteBufferUtils.copy(src);
        
        assertEquals(src.hashCode(), dst.hashCode());
        assertTrue(dst.isDirect());
    }
    
    @Test
    public void testCopyDirectForced() {
        ByteBuffer src = allocateTestBuffer(100);
        ByteBuffer dst = ByteBufferUtils.copy(src, true);
        
        assertEquals(src.hashCode(), dst.hashCode());
        assertTrue(dst.isDirect());
    }
    
    @Test
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
    
    @Test
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
    
    @Test
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
    
    @Test
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

    @Test
    public void testNonEmpty() {
        ByteBuffer bb = ByteBuffer.allocate(1);
        assertFalse(ByteBufferUtils.isEmpty(bb));
    }
    
    @Test
    public void testEmpty() {
        ByteBuffer bb = ByteBuffer.allocate(0);
        assertTrue(ByteBufferUtils.isEmpty(bb));
        assertTrue(ByteBufferUtils.isEmpty(null));
    }
}
