/*
 ** 2014 Februar 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import info.ata4.io.Seekable;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class IOSocket implements Closeable {
    
    private final IOSocketInputStream isp;
    private final IOSocketOutputStream osp;
    private final IOSocketReadableByteChannel rbcp;
    private final IOSocketWritableByteChannel wbcp;
    
    private DataInput in;
    private DataOutput out;
    private Seekable seekable;
    private boolean canRead;
    private boolean canWrite;

    public IOSocket() {
        isp = new IOSocketInputStream(this);
        osp = new IOSocketOutputStream(this);
        rbcp = new IOSocketReadableByteChannel(this);
        wbcp = new IOSocketWritableByteChannel(this);
    }
    
    /**
     * Returns the readability flag for this socket.
     * 
     * @return true if this socket can be read from
     */
    public boolean canRead() {
        return canRead;
    }
    
    protected void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }
    
    /**
     * Returns the writability flag for this socket.
     * 
     * @return true if this socket can be written to
     */
    public boolean canWrite() {
        return canWrite;
    }
    
    protected void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }
    
    /**
     * Creates a new input stream from this socket. If any input stream was
     * previously created by this method, it will be closed automatically.
     * 
     * @return output stream for this socket
     */
    public InputStream getInputStream() {
        return isp.getInputStream();
    }
    
    protected InputStream getRawInputStream() {
        return isp.getRawInputStream();
    }
    
    protected void setRawInputStream(InputStream is) {
        isp.setRawInputStream(is);
    }
    
    /**
     * Creates a new output stream from this socket. If any output stream was
     * previously created by this method, it will be closed automatically.
     * 
     * @return output stream for this socket
     */
    public OutputStream getOutputStream() {
        return osp.getOutputStream();
    }
    
    protected OutputStream getRawOutputStream() {
        return osp.getRawOutputStream();
    }
    
    protected void setRawOutputStream(OutputStream os) {
        osp.setRawOutputStream(os);
    }
    
    protected DataInput newDataInput() {
        InputStream stream = getInputStream();
        if (stream != null) {
            return new DataInputStream(stream);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the DataInput instance for this socket. If it hasn't been created
     * yet, it will be created.
     * 
     * @return DataInput instance
     */
    public DataInput getDataInput() {
        if (in == null) {
            in = newDataInput();
        }
        return in;
    }
    
    protected void setDataInput(DataInput in) {
        this.in = in;
    }
    
    protected DataOutput newDataOutput() {
        OutputStream stream = getOutputStream();
        if (stream != null) {
            return new DataOutputStream(stream);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the DataOutput instance for this socket. If it hasn't been created
     * yet, it will be created.
     * 
     * @return DataOutput instance
     */
    public DataOutput getDataOutput() {
        if (out == null) {
            out = newDataOutput();
        }
        return out;
    }
    
    protected void setDataOutput(DataOutput out) {
        this.out = out;
    }
    
    public ReadableByteChannel getReadableByteChannel() {
        return rbcp.getChannel();
    }
    
    protected ReadableByteChannel getRawReadableByteChannel() {
        return rbcp.getRawChannel();
    }
    
    protected void setRawReadableByteChannel(ReadableByteChannel wchan) {
        rbcp.setRawChannel(wchan);
    }

    public WritableByteChannel getWritableByteChannel() {
        return wbcp.getChannel();
    }
    
    protected WritableByteChannel getRawWritableByteChannel() {
        return wbcp.getRawChannel();
    }
    
    protected void setRawWritableByteChannel(WritableByteChannel wchan) {
        wbcp.setRawChannel(wchan);
    }
    
    public ByteBuffer getByteBuffer() {
        return null;
    }
    
    public Seekable getSeekable() {
        return seekable;
    }
    
    protected void setSeekable(Seekable seekable) {
        this.seekable = seekable;
    }

    @Override
    public void close() throws IOException {
        close(isp);
        close(osp);
        close(rbcp);
        close(wbcp);
    }
    
    protected void close(Closeable c) throws IOException {
        if (c != null) {
            c.close();
        }
    }
}
