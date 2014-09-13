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

import info.ata4.io.Positionable;
import info.ata4.io.Seekable;
import info.ata4.io.Swappable;
import info.ata4.io.socket.provider.DataInputProvider;
import info.ata4.io.socket.provider.DataOutputProvider;
import info.ata4.io.socket.provider.InputStreamProvider;
import info.ata4.io.socket.provider.OutputStreamProvider;
import info.ata4.io.socket.provider.ReadableByteChannelProvider;
import info.ata4.io.socket.provider.WritableByteChannelProvider;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
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
    
    private InputStreamProvider isp;
    private OutputStreamProvider osp;
    private ReadableByteChannelProvider rbcp;
    private WritableByteChannelProvider wbcp;
    private DataInputProvider dip;
    private DataOutputProvider dop;
    
    private Positionable positionable;
    private Swappable swappable;
    private boolean canRead;
    private boolean canWrite;
    
    protected InputStreamProvider getInputStreamProvider() {
        if (isp == null) {
            isp = new InputStreamProvider(this);
        }
        return isp;
    }
    
    protected OutputStreamProvider getOutputStreamProvider() {
        if (osp == null) {
            osp = new OutputStreamProvider(this);
        }
        return osp;
    }
    
    protected ReadableByteChannelProvider getReadableByteChannelProvider() {
        if (rbcp == null) {
            rbcp = new ReadableByteChannelProvider(this);
        }
        return rbcp;
    }
    
    protected WritableByteChannelProvider getWritableByteChannelProvider() {
        if (wbcp == null) {
            wbcp = new WritableByteChannelProvider(this);
        }
        return wbcp;
    }
    
    protected DataInputProvider getDataInputProvider() {
        if (dip == null) {
            dip = new DataInputProvider(this);
        }
        return dip;
    }
    
    protected DataOutputProvider getDataOutputProvider() {
        if (dop == null) {
            dop = new DataOutputProvider(this);
        }
        return dop;
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
        return getInputStreamProvider().get();
    }
    
    /**
     * Creates a new output stream from this socket. If any output stream was
     * previously created by this method, it will be closed automatically.
     * 
     * @return output stream for this socket
     */
    public OutputStream getOutputStream() {
        return getOutputStreamProvider().get();
    }
    
    /**
     * Returns the DataInput instance for this socket. If it hasn't been created
     * yet, it will be created.
     * 
     * @return DataInput instance
     */
    public DataInput getDataInput() {
        return getDataInputProvider().get();
    }
    
    /**
     * Returns the DataOutput instance for this socket. If it hasn't been created
     * yet, it will be created.
     * 
     * @return DataOutput instance
     */
    public DataOutput getDataOutput() {
        return getDataOutputProvider().get();
    }
    
    public ReadableByteChannel getReadableByteChannel() {
        return getReadableByteChannelProvider().get();
    }
    
    public WritableByteChannel getWritableByteChannel() {
        return getWritableByteChannelProvider().get();
    }
    
    public ByteBuffer getByteBuffer() {
        return null;
    }
    
    public Positionable getPositionable() {
        return positionable;
    }
    
    protected void setPositionable(Positionable positionable) {
        this.positionable = positionable;
    }

    public Swappable getSwappable() {
        return swappable;
    }

    public void setSwappable(Swappable swappable) {
        this.swappable = swappable;
    }

    @Override
    public void close() throws IOException {
        close(isp);
        close(osp);
        close(rbcp);
        close(wbcp);
        close(dip);
        close(dop);
    }
    
    protected void close(Closeable c) throws IOException {
        if (c != null) {
            c.close();
        }
    }
}
