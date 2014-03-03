/*
 ** 2013 December 5
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import static java.nio.channels.FileChannel.MapMode.*;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ByteBuffer utility class.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ByteBufferUtils {
    
    private static final ByteBuffer EMPTY = ByteBuffer.allocate(0);
    private static final int DIRECT_THRESHOLD = 10240; // 10 KB

    private ByteBufferUtils() {
    }
    
    private static int getLength(FileChannel fc, int length) throws IOException {
        if (length < 0) {
            return (int) Math.min(fc.size(), Integer.MAX_VALUE);
        } else {
            return length;
        }
    }
    
    private static ByteBuffer allocate(int size) {
        // allocateDirect is pretty slow when used frequently, use it for larger
        // buffers only
        if (size > DIRECT_THRESHOLD) {
            return ByteBuffer.allocateDirect(size);
        } else {
            try {
                return ByteBuffer.allocate(size);
            } catch (OutOfMemoryError ex) {
                // not enough space in the heap, try direct allocation instead
                return ByteBuffer.allocateDirect(size);
            }
        }
    }
    
    public static ByteBuffer load(Path path, int offset, int length) throws IOException {
        try (FileChannel fc = FileChannel.open(path, READ)) {
            ByteBuffer bb = allocate(getLength(fc, length));
            fc.position(offset);
            fc.read(bb);
            return bb;
        }
    }

    public static ByteBuffer load(Path path) throws IOException {
        return load(path, 0, -1);
    }
    
    public static ByteBuffer load(List<Path> paths) throws IOException {
        long size = 0;
        Map<Path, FileChannel> channelMap = new HashMap<>();
        
        try {
            for (Path path : paths) {
                FileChannel fc = FileChannel.open(path, READ);
                size += Math.max(fc.size(), Integer.MAX_VALUE);
                channelMap.put(path, fc);
            }

            if (size > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Files are too large to load");
            }

            ByteBuffer bb = allocate((int) size);

            for (Path path : paths) {
                channelMap.get(path).read(bb);
            }

            bb.flip();

            return bb;
        } finally {
            for (FileChannel fc : channelMap.values()) {
                fc.close();
            }
        }
    }
    
    public static void save(Path path, ByteBuffer bb) throws IOException {
        try (FileChannel fc = FileChannel.open(path, WRITE, CREATE)) {
            fc.write(bb);
        }
    }
        
    public static MappedByteBuffer openReadOnly(Path path, int offset, int length) throws IOException {
        try (FileChannel fc = FileChannel.open(path, READ)) {
            return fc.map(READ_ONLY, offset, getLength(fc, length));
        }
    }
    
    public static MappedByteBuffer openReadOnly(Path path) throws IOException {
        return openReadOnly(path, 0, -1);
    }
    
    public static MappedByteBuffer openReadWrite(Path path, int offset, int length) throws IOException {
        try (FileChannel fc = FileChannel.open(path, READ, WRITE, CREATE)) {
            return fc.map(READ_WRITE, offset, getLength(fc, length));
        }
    }
    
    public static MappedByteBuffer openReadWrite(Path path) throws IOException {
        return openReadWrite(path, 0, -1);
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
        
        // set same byte order
        bbSlice.order(bb.order());
        
        // restore original limit and position
        bb.limit(limit);
        bb.position(pos);
        
        return bbSlice;
    }
    
    public static ByteBuffer getSlice(ByteBuffer bb, int offset) {
        return getSlice(bb, offset, -1);
    }
    
    public static ByteBuffer concat(List<ByteBuffer> bbs) {
        int length = 0;
        
        // get amount of remaining bytes from all buffers
        for (ByteBuffer bb : bbs) {
            bb.rewind();
            length += bb.remaining();
        }
        
        if (length == 0) {
            // very funny
            return EMPTY;
        }
        
        ByteBuffer bbNew = ByteBuffer.allocateDirect(length);
        
        // put all buffers from list
        for (ByteBuffer bb : bbs) {
            bb.rewind();
            bbNew.put(bb);
        }
        
        bbNew.rewind();
        
        return bbNew;
    }
}
