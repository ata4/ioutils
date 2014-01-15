/*
 ** 2014 Januar 15
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.util.io.file;

import info.ata4.util.io.ByteBufferUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

/**
 * Basic handler that can read and write files and also read from byte buffers.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class FileHandler {
    
    private Path sourceFile;
    
    public void open(Path file) throws IOException {
        load(ByteBufferUtils.openReadOnly(file));
        sourceFile = file;
    }

    public void load(Path file) throws IOException {
        load(ByteBufferUtils.load(file));
        sourceFile = file;
    }
    
    public abstract void load(ByteBuffer bb) throws IOException;

    public abstract void save(Path file) throws IOException;

    public Path getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(Path sourceFile) {
        this.sourceFile = sourceFile;
    }
}
