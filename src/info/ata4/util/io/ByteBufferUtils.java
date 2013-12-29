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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
    
    public static void load(Path path, int offset, int length, ByteBuffer dest) throws IOException {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            fc.read(dest, offset);
        }
    }
    
    public static void load(File file, int offset, int length, ByteBuffer dest) throws IOException {
        load(file.toPath(), offset, length, dest);
    }
    
    public static void save(Path path, ByteBuffer bb) throws IOException {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            fc.write(bb);
        }
    }
    
    public static void save(File file, ByteBuffer bb) throws IOException {
        save(file.toPath(), bb);
    }
        
    public static ByteBuffer openReadOnly(Path path, int offset, int length) throws IOException {
        ByteBuffer bb;
        
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            // map entire file as byte buffer
            bb = fc.map(FileChannel.MapMode.READ_ONLY, offset, length > 0 ? length : fc.size());
        }
        
        return bb;
    }
    
    public static ByteBuffer openReadOnly(File file, int offset, int length) throws IOException {
        return openReadOnly(file.toPath(), offset, length);
    }
    
    public static ByteBuffer openReadOnly(Path path) throws IOException {
        return openReadOnly(path, 0, 0);
    }
    
    public static ByteBuffer openReadOnly(File file) throws IOException {
        return openReadOnly(file.toPath());
    }
    
    public static ByteBuffer openReadWrite(Path path, int offset, int size) throws IOException {
        ByteBuffer bb;
        
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            if (size > 0 && size != fc.size()) {
                // reset file if a new size is set
                fc.truncate(0);
            }
            
            // map file as byte buffer
            bb = fc.map(FileChannel.MapMode.READ_WRITE, offset, size);
        }
        
        return bb;
    }
    
    public static ByteBuffer openReadWrite(File file, int offset, int size) throws IOException {
        return openReadWrite(file.toPath(), offset, size);
    }
    
    public static ByteBuffer openReadWrite(Path path) throws IOException {
        return openReadWrite(path, 0, 0);
    }
    
    public static ByteBuffer openReadWrite(File file) throws IOException {
        return openReadWrite(file.toPath());
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
