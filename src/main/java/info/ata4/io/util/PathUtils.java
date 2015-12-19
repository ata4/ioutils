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
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class smiliar to org.apache.commons.io.FileUtils, but for Java NIO.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class PathUtils {
    
    private PathUtils() {
    }
    
    @Deprecated
    public static String getName(Path path) {
        return getFileName(path);
    }
        
    public static String getFileName(Path path) {
        return path.getFileName().toString();
    }
    
    public static Path setFileName(Path path, String name) {
        return path.resolveSibling(name);
    }
    
    public static String getBaseName(Path path) {
        return FilenameUtils.getBaseName(path.getFileName().toString());
    }
    
    public static Path setBaseName(Path path, String name) {
        String ext = getExtension(path);
        String fileName = name;
        if (!StringUtils.isEmpty(ext)) {
            fileName += '.' + ext;
        }
        return setFileName(path, fileName);
    }
    
    public static String getExtension(Path path) {
        return FilenameUtils.getExtension(getFileName(path));
    }
    
    public static Path setExtension(Path path, String ext) {
        String fileName = getBaseName(path);
        if (!StringUtils.isEmpty(ext)) {
            fileName += '.' + ext;
        }
        return setFileName(path, fileName);
    }

    public static Path removeExtension(Path path) {
        return setExtension(path, null);
    }
    
    public static Path append(Path path, String append) {
        return setFileName(path, getFileName(path) + append);
    }
    
    public static Path appendBaseName(Path path, String append) {
        return setBaseName(path, getBaseName(path) + append);
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
    
    public static Path getCodeSourceLocation(Class clazz) {
        try {
            return Paths.get(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ex) {
            return null;
        }
    }
}
