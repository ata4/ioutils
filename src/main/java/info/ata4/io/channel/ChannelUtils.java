/*
 ** 2015 March 09
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.channel;

import info.ata4.io.buffer.ByteBufferUtils;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class ChannelUtils {
    
    public static boolean isReadable(Channel c) {
        if (!(c instanceof ReadableByteChannel)) {
            return false;
        }
        
        try {
            // try to read an empty buffer
            ReadableByteChannel rbc = (ReadableByteChannel) c;
            rbc.read(ByteBufferUtils.EMPTY);
            return true;
        } catch (NonReadableChannelException ex) {
            return false;
        } catch (IOException ex) {
            throw new RuntimeException("Broken channel", ex);
        }
    }
    
    public static boolean isWritable(Channel c) {
        if (!(c instanceof WritableByteChannel)) {
            return false;
        }
        
        try {
            // try to write an empty buffer
            WritableByteChannel wbc = (WritableByteChannel) c;
            wbc.write(ByteBufferUtils.EMPTY);
            return true;
        } catch (NonWritableChannelException ex) {
            return false;
        } catch (IOException ex) {
            throw new RuntimeException("Broken channel", ex);
        }
    }
    
    private ChannelUtils() {
    }
}
