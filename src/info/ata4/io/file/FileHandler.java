/*
 ** 2014 Januar 15
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.file;

import info.ata4.io.DataInputReader;
import info.ata4.io.DataOutputWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Basic handler that can read and write files and also read from byte buffers.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public abstract class FileHandler {
    
    protected Path sourceFile;

    public void load(Path file) throws IOException {
        sourceFile = file;
        load(DataInputReader.newReader(file));
    }
    
    public abstract void load(DataInputReader in) throws IOException;
    
    public void save(Path file) throws IOException {
        sourceFile = file;
        save(DataOutputWriter.newWriter(file));
    }

    public abstract void save(DataOutputWriter in) throws IOException;

    public Path getSourceFile() {
        return sourceFile;
    }
}
