/*
 ** 2011 August 20
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

/**
 * Utility class to open files via NIO buffers.
 * 
 * @deprecated use ByteBufferUtils instead
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
@Deprecated
public class NIOFileUtils {
    
    private NIOFileUtils() {
    }
    
    public static ByteBuffer load(File file) throws IOException {
        return ByteBufferUtils.load(file);
    }
    
    public static ByteBuffer load(File file, int offset, int length) throws IOException {
        return ByteBufferUtils.load(file, offset, length);
    }
    
    public static void load(File file, int offset, int length, ByteBuffer dest) throws IOException {
        ByteBufferUtils.load(file, offset, length, dest);
    }
    
    public static void save(File file, ByteBuffer bb) throws IOException {
        ByteBufferUtils.save(file, bb);
    }
    
    public static ByteBuffer openReadOnly(File file) throws IOException {
        return ByteBufferUtils.openReadOnly(file);
    }
    
    public static ByteBuffer openReadOnly(File file, int offset, int length) throws IOException {
        return ByteBufferUtils.openReadOnly(file, offset, length);
    }
    
    public static ByteBuffer openReadWrite(File file) throws IOException {
        return ByteBufferUtils.openReadWrite(file);
    }
    
    public static ByteBuffer openReadWrite(File file, int offset, int size) throws IOException {
        return ByteBufferUtils.openReadWrite(file, offset, size);
    }
}
