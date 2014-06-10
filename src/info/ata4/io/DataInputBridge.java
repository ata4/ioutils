/*
 ** 2013 July 10
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import info.ata4.io.socket.IOSocket;
import java.io.DataInput;
import java.io.IOException;
import org.apache.commons.io.EndianUtils;

/**
 * IO bridge that implements DataInput.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class DataInputBridge extends IOBridge implements DataInput {
    
    private final DataInput in;

    public DataInputBridge(IOSocket socket) {
        super(socket);
        in = socket.getDataInput();
    }
 
    @Override
    public void readFully(byte[] b) throws IOException {
        in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return in.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return in.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return in.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        short r = in.readShort();
        if (isManualSwap()) {
            r = EndianUtils.swapShort(r);
        }
        return r;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        int r = in.readUnsignedShort();
        if (isManualSwap()) {
            r = EndianUtils.swapShort((short) r) & 0xffff;
        }
        return r;
    }

    @Override
    public char readChar() throws IOException {
        return in.readChar();
    }

    @Override
    public int readInt() throws IOException {
        int r = in.readInt();
        if (isManualSwap()) {
            r = EndianUtils.swapInteger(r);
        }
        return r;
    }

    @Override
    public long readLong() throws IOException {
        long r = in.readLong();
        if (isManualSwap()) {
            r = EndianUtils.swapLong(r);
        }
        return r;
    }

    @Override
    public float readFloat() throws IOException {
        if (isManualSwap()) {
            // NOTE: don't use readFloat() plus EndianUtils.swapFloat() here!
            return Float.intBitsToFloat(readInt());
        } else {
            return in.readFloat();
        }
    }

    @Override
    public double readDouble() throws IOException {
        if (isManualSwap()) {
            // NOTE: don't use readDouble() plus EndianUtils.swapDouble() here!
            return Double.longBitsToDouble(readLong());
        } else {
            return in.readDouble();
        }
    }

    @Override
    public String readLine() throws IOException {
        return in.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return in.readUTF();
    }
}
