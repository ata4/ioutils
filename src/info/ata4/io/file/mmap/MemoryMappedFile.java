/*
 ** 2014 September 12
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.file.mmap;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import static java.nio.channels.FileChannel.MapMode.*;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class MemoryMappedFile {
    
    /**
     * Maximum size for one memory-mapped page. Integer.MAX_VALUE isn't recommended,
     * since it's unaligned and may cause troubles on 32-bit systems.
     */
    private static final int PAGE_SIZE = 1 << 30; // 1 GiB
    
    /**
     * Number of overlapping bytes between pages. Should be equal to the length
     * of the largest datatype that can be read with a byte buffer, which is
     * long/double.
     */
    private static final int PAGE_MARGIN = 8;
    
    private final ByteBuffer[] buffers;
    private final long size;
    
    private long position;
    private ByteOrder order = ByteOrder.BIG_ENDIAN;
    
    public MemoryMappedFile(Path path, OpenOption... options) throws IOException {
        Set<OpenOption> optionsSet = new HashSet<>(Arrays.asList(options));
        MapMode mapMode = optionsSet.contains(WRITE) ? READ_WRITE : READ_ONLY;
        
        try (FileChannel fc = FileChannel.open(path, options)) {
            size = fc.size();
            int bufferCount = (int) (size / PAGE_SIZE) + 1;
            long bufferOfs = 0;

            buffers = new ByteBuffer[bufferCount];
            for (int i = 0; i < bufferCount; i++) {
                long remaining = size - bufferOfs;
                long bufferLen1 = (int) Math.min(PAGE_SIZE - PAGE_MARGIN, remaining);
                long bufferLen2 = (int) Math.min(PAGE_SIZE, remaining);
                buffers[i] = fc.map(mapMode, bufferOfs, bufferLen2);
                bufferOfs += bufferLen1;
            }
        }
    }

    public long getSize() {
        return size;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long newpos) {
        if (newpos < 0 || newpos > size) {
            throw new IllegalArgumentException();
        }
        
        position = newpos;
    }
    
    public long getRemaining() throws IOException {
        return size - position;
    }

    public boolean hasRemaining() throws IOException {
        return getRemaining() > 0;
    }
    
    public void order(ByteOrder bo) {
        order = bo;
        for (ByteBuffer buffer : buffers) {
            buffer.order(order);
        }
    }
    
    public ByteOrder order() {
        return order;
    }
    
    private int getPage(long offset) {
        return (int) (offset / PAGE_SIZE);
    }

    private int getIndex(long offset) {
        return (int) (offset % PAGE_SIZE);
    }
    
    private ByteBuffer getBuffer(long offset) {
        return buffers[getPage(offset)];
    }
    
    // array get
    
    public void get(byte[] dst, int offset, int length) {
        while (length > 0) {
            ByteBuffer bb = getBuffer(position);
            bb.position(getIndex(position));
            int lengthChunk = Math.min(length, bb.remaining());
            bb.get(dst, offset, lengthChunk);
            position += lengthChunk;
            offset += lengthChunk;
            length -= lengthChunk;
        }
    }
    
    public void get(byte[] dst) {
        get(dst, 0, dst.length);
    }
    
    public void get(ByteBuffer dst) {
        int n = dst.remaining();
        for (int i = 0; i < n; i++) {
            dst.put(get());
        }
    }
    
    // array put
    
    public void put(byte[] src, int offset, int length) {
        while (length > 0) {
            ByteBuffer bb = getBuffer(position);
            bb.position(getIndex(position));
            int lengthChunk = Math.min(length, bb.remaining());
            bb.put(src, offset, lengthChunk);
            position += lengthChunk;
            offset += lengthChunk;
            length -= lengthChunk;
        }
    }
    
    public void put(byte[] src) {
        put(src, 0, src.length);
    }
    
    public void put(ByteBuffer src) {
        int n = src.remaining();
        for (int i = 0; i < n; i++) {
            put(src.get());
        }
    }
    
    // byte get

    public byte get(long offset) {
        return getBuffer(offset).get(getIndex(offset));
    }
    
    public byte get() {
        return get(position++);
    }
    
    // byte put
    
    public void put(long offset, byte b) {
        getBuffer(offset).put(getIndex(offset), b);
    }
    
    public void put(byte b) {
        put(position++, b);
    }
    
    // char get
    
    public char getChar(long offset) {
        return getBuffer(position).getChar(getIndex(offset));
    }
    
    public char getChar() {
        char v = getChar(position);
        position += 2;
        return v;
    }
    
    // char put
    
    public void putChar(long offset, char value) {
        getBuffer(offset).putChar(getIndex(offset), value);
    }
    
    public void putChar(char value) {
        putChar(position, value);
        position += 2;
    }
    
    // short get
    
    public short getShort(long offset) {
        return getBuffer(position).getShort(getIndex(offset));
    }
    
    public short getShort() {
        short v = getShort(position);
        position += 2;
        return v;
    }
    
    // short put
    
    public void putShort(long offset, short value) {
        getBuffer(offset).putShort(getIndex(offset), value);
    }
    
    public void putShort(short value) {
        putShort(position, value);
        position += 2;
    }
    
    // int get
    
    public int getInt(long offset) {
        return getBuffer(position).getInt(getIndex(offset));
    }
    
    public int getInt() {
        int v = getInt(position);
        position += 4;
        return v;
    }
    
    // int put
    
    public void putInt(long offset, int value) {
        getBuffer(offset).putInt(getIndex(offset), value);
    }
    
    public void putInt(int value) {
        putInt(position, value);
        position += 4;
    }
    
    // long get
    
    public long getLong(long offset) {
        return getBuffer(position).getLong(getIndex(offset));
    }
    
    public long getLong() {
        long v = getLong(position);
        position += 8;
        return v;
    }
    
    // long put
    
    public void putLong(long offset, long value) {
        getBuffer(offset).putLong(getIndex(offset), value);
    }
    
    public void putLong(long value) {
        putLong(position, value);
        position += 8;
    }
    
    // float get
    
    public float getFloat(long offset) {
        return getBuffer(position).getFloat(getIndex(offset));
    }
    
    public float getFloat() {
        float v = getFloat(position);
        position += 4;
        return v;
    }
    
    // float put
    
    public void putFloat(long offset, float value) {
        getBuffer(offset).putFloat(getIndex(offset), value);
    }
    
    public void putFloat(float value) {
        putFloat(position, value);
        position += 4;
    }
    
    // double get
    
    public double getDouble(long offset) {
        return getBuffer(position).getDouble(getIndex(offset));
    }
    
    public double getDouble() {
        double v = getDouble(position);
        position += 8;
        return v;
    }
    
    // double put
    
    public void putDouble(long offset, double value) {
        getBuffer(offset).putDouble(getIndex(offset), value);
    }
    
    public void putDouble(double value) {
        putDouble(position, value);
        position += 8;
    }
}
