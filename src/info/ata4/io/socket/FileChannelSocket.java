/*
 ** 2014 February 22
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.socket;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FileChannelSocket extends ChannelSocket {
    
    public FileChannelSocket(Path file, OpenOption... options) throws IOException {
        this(FileChannel.open(file, options), options);
    }
    
    public FileChannelSocket(FileChannel fc, OpenOption... options) {
        super(fc);
        
        Set<OpenOption> optionSet = new HashSet<>(Arrays.asList(options));
        setCanRead(optionSet.contains(READ));
        setCanWrite(optionSet.contains(WRITE));
    }
}
