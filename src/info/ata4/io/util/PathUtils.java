/*
 ** 2014 July 17
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FilenameUtils;

/**
 * Utility class smiliar to org.apache.commons.io.FileUtils, but for Java NIO.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class PathUtils {
    
    private PathUtils() {
    }
    
    public static String getBaseName(Path path) {
        return FilenameUtils.getBaseName(path.getFileName().toString());
    }
    
    public static String getExtension(Path path) {
        return FilenameUtils.getExtension(path.getFileName().toString());
    }
    
    public static Path changeExtension(Path path, String ext) {
        String name = path.getFileName().toString();
        name = FilenameUtils.removeExtension(name);
        if (ext == null) {
            return path.resolveSibling(name);
        } else {
            return path.resolveSibling(name + '.' + ext);
        }
    }

    public static Path removeExtension(Path path) {
        return changeExtension(path, null);
    }
    
    public static Path append(Path path, String ext) {
        return path.resolveSibling(path.getFileName().toString() + ext);
    }
    
    public static boolean isDirectoryEmpty(Path path) {
        if (!Files.isDirectory(path)) {
            return false;
        }
        
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
            return !ds.iterator().hasNext();
        } catch (IOException ex) {
            return false;
        }
    }
    
    public static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
        }
    }
}
