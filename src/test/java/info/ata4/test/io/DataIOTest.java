/*
** 2015 Dezember 11
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package info.ata4.test.io;

import info.ata4.io.DataReader;
import info.ata4.io.DataReaders;
import info.ata4.io.DataWriter;
import info.ata4.io.DataWriters;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import org.apache.commons.lang3.RandomStringUtils;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
@RunWith(Parameterized.class)
public class DataIOTest {
    
    @Parameterized.Parameters
    public static List<Object[]> data() throws IOException {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[] {ByteOrder.LITTLE_ENDIAN});
        data.add(new Object[] {ByteOrder.BIG_ENDIAN});
        return data;
    }
    
    private static final int BUFFER_SIZE = 128;
    private static final int TEST_CYCLES = 1000;
    private final Random r = new Random();
    private final ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
    private final DataReader in = DataReaders.forByteBuffer(buf);
    private final DataWriter out = DataWriters.forByteBuffer(buf);
    
    public DataIOTest(ByteOrder order) {
        buf.order(order);
    }
    
    @Test
    public void testByteOrder() {
        assertEquals(buf.order(), in.order());
        assertEquals(in.order(), out.order());
    }
    
    @Test
    public void testBytes() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            byte[] value = new byte[BUFFER_SIZE];
            r.nextBytes(value);
            out.position(0);
            out.writeBytes(value);

            byte[] valueRead = new byte[BUFFER_SIZE];
            in.position(0);
            in.readBytes(valueRead);
            assertArrayEquals(value, valueRead);
        }
    }
    
    @Test
    public void testBytesWithOffset() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            int offset = r.nextInt(BUFFER_SIZE);
            int length = r.nextInt(BUFFER_SIZE - offset);

            byte[] value = new byte[BUFFER_SIZE];
            r.nextBytes(value);
            Arrays.fill(value, 0, offset, (byte) 0);
            Arrays.fill(value, offset + length, BUFFER_SIZE, (byte) 0);
            out.position(0);
            out.writeBytes(value, offset, length);

            byte[] valueRead = new byte[BUFFER_SIZE];
            in.position(0);
            in.readBytes(valueRead, offset, length);
            assertArrayEquals(value, valueRead);
        }
    }
    
    @Test
    public void testBuffer() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            byte[] valueRaw = new byte[BUFFER_SIZE];
            r.nextBytes(valueRaw);
            ByteBuffer value = ByteBuffer.wrap(valueRaw);
            out.position(0);
            out.writeBuffer(value);

            ByteBuffer valueRead = ByteBuffer.allocate(BUFFER_SIZE);
            in.position(0);
            in.readBuffer(valueRead);

            assertEquals(value, valueRead);
            assertEquals(value, buf);
        }
    }
    
    @Test
    public void testByte() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            byte[] valueRaw = new byte[1];
            r.nextBytes(valueRaw);
            byte value = valueRaw[0];
            out.position(0);
            out.writeByte(value);
            in.position(0);
            assertEquals(value, in.readByte());
        }
    }
    
    @Test
    public void testUnsignedByte() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            byte[] valueRaw = new byte[1];
            r.nextBytes(valueRaw);
            int value = valueRaw[0] & 0xff;
            out.position(0);
            out.writeUnsignedByte(value);
            in.position(0);
            assertEquals(value, in.readUnsignedByte());
        }
    }
    
    @Test
    public void testBoolean() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            boolean value = r.nextBoolean();
            out.position(0);
            out.writeBoolean(value);
            in.position(0);
            assertEquals(value, in.readBoolean());
        }
    }
    
    @Test
    public void testChar() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            char value = RandomStringUtils.random(1).charAt(0);
            out.position(0);
            out.writeChar(value);
            in.position(0);
            assertEquals(value, in.readChar());
        }
    }
    
    @Test
    public void testShort() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            short value = (short) (r.nextInt() & 0xffff);
            out.position(0);
            out.writeShort(value);
            in.position(0);
            assertEquals(value, in.readShort());
        }
    }
    
    @Test
    public void testUnsignedShort() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            int value = r.nextInt() & 0xffff;
            out.position(0);
            out.writeUnsignedShort(value);
            in.position(0);
            assertEquals(value, in.readUnsignedShort());
        }
    }
    
    @Test
    public void testInt() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            int value = r.nextInt();
            out.position(0);
            out.writeInt(value);
            in.position(0);
            assertEquals(value, in.readInt());
        }
    }
    
    @Test
    public void testUnsignedInt() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            long value = r.nextInt() & 0xffffffffL;
            out.position(0);
            out.writeUnsignedInt(value);
            in.position(0);
            assertEquals(value, in.readUnsignedInt());
        }
    }
    
    @Test
    public void testLong() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            long value = r.nextLong();
            out.position(0);
            out.writeLong(value);
            in.position(0);
            assertEquals(value, in.readLong());
        }
    }
    
    @Test
    public void testUnsignedLong() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            BigInteger value = new BigInteger(64, r);
            out.position(0);
            out.writeUnsignedLong(value);
            in.position(0);
            
            BigInteger valueRead = in.readUnsignedLong();
            assertEquals(value, valueRead);
        }
    }
    
    @Test
    public void testHalf() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            float value = r.nextFloat();
            out.position(0);
            out.writeHalf(value);
            in.position(0);
            
            // set higher delta, since floats are read and written with lower
            // precision
            assertEquals(value, in.readHalf(), 0.0004);
        }
    }
    
    @Test
    public void testFloat() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            float value = r.nextFloat();
            out.position(0);
            out.writeFloat(value);
            in.position(0);
            assertEquals(value, in.readFloat(), 0);
        }
    }
    
    @Test
    public void testDouble() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            double value = r.nextDouble();
            out.position(0);
            out.writeDouble(value);
            in.position(0);
            assertEquals(value, in.readDouble(), 0);
        }
    }
    
    @Test
    public void testStringFixed() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            int length = r.nextInt(32);
            Charset cs = r.nextBoolean() ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8;
            String value = RandomStringUtils.randomAscii(length);
            out.position(0);
            out.writeStringFixed(value, cs);
            in.position(0);
            assertEquals(value, in.readStringFixed(length, cs));
        }
    }
    
    @Test
    public void testStringNull() throws IOException {
        for (int i = 0; i < TEST_CYCLES; i++) {
            int length = r.nextInt(32);
            Charset cs = r.nextBoolean() ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8;
            String value = RandomStringUtils.randomAscii(length);
            out.position(0);
            out.writeStringNull(value, cs);
            in.position(0);
            assertEquals(value, in.readStringNull(length * 2, cs));
        }
    }
    
    @Test
    public void testStringPrefixed() throws IOException {
        List<Class> types = new ArrayList<>();
        types.add(Byte.TYPE);
        types.add(Short.TYPE);
        types.add(Integer.TYPE);
        
        for (int i = 0; i < TEST_CYCLES; i++) {
            int length = r.nextInt(32);
            Charset cs = r.nextBoolean() ? StandardCharsets.US_ASCII : StandardCharsets.UTF_8;
            Class type = types.get(r.nextInt(types.size()));
            String value = RandomStringUtils.randomAscii(length);
            out.position(0);
            out.writeStringPrefixed(value, type, cs);
            in.position(0);
            assertEquals(value, in.readStringPrefixed(type, length * 2, cs));
        }
    }
}
