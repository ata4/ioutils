/*
** 2015 Dezember 19
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package info.ata4.test.io.util;

import info.ata4.io.util.PathUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
@RunWith(Parameterized.class)
public class PathUtilsTest {
    
    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> data = new ArrayList<>();
        data.add(new Object[] {"/unix/path/with/", "file", "txt"});
        data.add(new Object[] {"/unix/path/with/", "file", ""});
        data.add(new Object[] {"C:\\windows\\path\\with\\", "file", "txt"});
        data.add(new Object[] {"C:\\windows\\path\\with\\", "file", ""});
        return data;
    }
    
    private final String dir;
    private final String baseName;
    private final String ext;
    private final String extFull;
    private final String fileName;
    private final Path path;
    
    public PathUtilsTest(String dir, String baseName, String ext) {
        this.dir = dir;
        this.baseName = baseName;
        this.ext = ext;
        
        if (StringUtils.isEmpty(ext)) {
            extFull = "";
        } else {
            extFull = '.' + ext;
        }
        
        fileName = baseName + extFull;
        path = Paths.get(dir, fileName);
    }

    /**
     * Test of getFileName method, of class PathUtils.
     */
    @Test
    public void testGetFileName() {
        System.out.println("getFileName");
        String result = PathUtils.getFileName(path);
        assertEquals(fileName, result);
    }

    /**
     * Test of setFileName method, of class PathUtils.
     */
    @Test
    public void testSetFileName() {
        System.out.println("setFileName");
        String name = "image.jpg";
        Path expResult = Paths.get(dir, name);
        Path result = PathUtils.setFileName(path, name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getBaseName method, of class PathUtils.
     */
    @Test
    public void testGetBaseName() {
        System.out.println("getBaseName");
        String result = PathUtils.getBaseName(path);
        assertEquals(baseName, result);
    }

    /**
     * Test of setBaseName method, of class PathUtils.
     */
    @Test
    public void testSetBaseName() {
        System.out.println("setBaseName");
        String name = "document";
        Path expResult = Paths.get(dir, name + extFull);
        Path result = PathUtils.setBaseName(path, name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getExtension method, of class PathUtils.
     */
    @Test
    public void testGetExtension() {
        System.out.println("getExtension");
        String result = PathUtils.getExtension(path);
        assertEquals(ext, result);
    }

    /**
     * Test of setExtension method, of class PathUtils.
     */
    @Test
    public void testSetExtension() {
        System.out.println("setExtension");
        String newExt = "gif";
        Path expResult = Paths.get(dir, baseName + '.' + newExt);
        Path result = PathUtils.setExtension(path, newExt);
        assertEquals(expResult, result);
    }

    /**
     * Test of removeExtension method, of class PathUtils.
     */
    @Test
    public void testRemoveExtension() {
        System.out.println("removeExtension");
        Path expResult = Paths.get(dir, baseName);
        Path result = PathUtils.removeExtension(path);
        assertEquals(expResult, result);
    }

    /**
     * Test of appendFileName method, of class PathUtils.
     */
    @Test
    public void testAppend() {
        System.out.println("append");
        String append = "foo";
        Path expResult = Paths.get(dir, fileName + append);
        Path result = PathUtils.append(path, append);
        assertEquals(expResult, result);
    }

    /**
     * Test of appendBaseName method, of class PathUtils.
     */
    @Test
    public void testAppendBaseName() {
        System.out.println("appendBaseName");
        String append = "foo";
        Path expResult = Paths.get(dir, baseName + append + extFull);
        Path result = PathUtils.appendBaseName(path, append);
        assertEquals(expResult, result);
    }    
}
