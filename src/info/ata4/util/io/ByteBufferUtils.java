/*
 ** 2013 December 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.apache.commons.io.IOUtils;

/**
 * ByteBuffer utility class.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferUtils {
    
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);

    private ByteBufferUtils() {
    }
    
    public static ByteBuffer load(File file) throws IOException {
        return load(file, 0, 0);
    }
    
    public static ByteBuffer load(File file, int offset, int length) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(length > 0 ? length : (int) file.length());
        
        // read file into the buffer
        load(file, offset, length, bb);
        
        // prepare buffer to be read from the start
        bb.rewind();
        
        return bb;
    }
    
    public static void load(File file, int offset, int length, ByteBuffer dest) throws IOException {
        FileChannel fc = null;

        try {
            // fill the buffer with the file channel
            fc = new FileInputStream(file).getChannel();
            fc.read(dest, offset);
        } finally {
            IOUtils.closeQuietly(fc);
        }
    }
    
    public static void save(File file, ByteBuffer bb) throws IOException {
        FileChannel fc = null;
        
        try {
            fc = new FileOutputStream(file).getChannel();
            fc.write(bb);
        } finally {
            IOUtils.closeQuietly(fc);
        }
    }
    
    public static ByteBuffer openReadOnly(File file) throws IOException {
        return openReadOnly(file, 0, 0);
    }
    
    public static ByteBuffer openReadOnly(File file, int offset, int length) throws IOException {
        ByteBuffer bb;
        FileChannel fc = null;
        
        try {
            fc = new FileInputStream(file).getChannel();
            // map entire file as byte buffer
            bb = fc.map(FileChannel.MapMode.READ_ONLY, offset, length > 0 ? length : fc.size());
        } finally {
            IOUtils.closeQuietly(fc);
        }
        
        return bb;
    }
    
    public static ByteBuffer openReadWrite(File file) throws IOException {
        return openReadWrite(file, 0, 0);
    }
    
    public static ByteBuffer openReadWrite(File file, int offset, int size) throws IOException {
        ByteBuffer bb;
        RandomAccessFile raf = null;
        
        try {
            // open random access file
            raf = new RandomAccessFile(file, "rw");
            
            // reset file if a new size is set
            if (size > 0) {
                raf.setLength(0);
                raf.setLength(offset + size);
            } else {
                size = (int) raf.length() - offset;
            }
            
            // get file channel
            FileChannel fc = raf.getChannel();
            // map file as byte buffer
            bb = fc.map(FileChannel.MapMode.READ_WRITE, offset, size);
        } finally {
            IOUtils.closeQuietly(raf);
        }
        
        return bb;
    }
    
    public static ByteBuffer getSlice(ByteBuffer bb, int offset, int length) {
        if (length == 0) {
            // very funny
            return EMPTY;
        }
        
        // get current position and limit
        int pos = bb.position();
        int limit = bb.limit();
        
        bb.position(offset);
        
        // set new limit if length is provided, use current limit otherwise
        if (length > 0) {
            bb.limit(offset + length);
        }
        
        // do the actual slicing
        ByteBuffer bbSlice = bb.slice();
        
        // restore original limit and position
        bb.limit(limit);
        bb.position(pos);
        
        return bbSlice;
    }
    
    public static ByteBuffer getSlice(ByteBuffer bb, int offset) {
        return getSlice(bb, offset, -1);
    }
}
