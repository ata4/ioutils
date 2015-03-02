/*
 ** 2014 July 11
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.file;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;

/**
 * Utility class to check and sanitize illegal file names.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class FilenameSanitizer {
    
    public static final Set<Character> ILLEGAL_CHARS;
    public static final Set<String> ILLEGAL_NAMES;
    
    static {
        Set<Character> illegalChars = new HashSet<>();
        Set<String> illegalNames = new HashSet<>();
        
        // non-printable special characters
        for (int i = 0; i < 31; i++) {
            illegalChars.add((char) i);
        }
        
        // path separators, pipes, wildcards, etc..
        Character[] chars = {'<', '>', '/', '\\', '|', '?', '*', ':'};
        illegalChars.addAll(Arrays.asList(chars));
        
        
        // directory pointers
        illegalNames.add(".");
        illegalNames.add("..");
        
        // http://msdn.microsoft.com/en-us/library/aa365247%28VS.85%29
        if (SystemUtils.IS_OS_WINDOWS) {
            illegalChars.add('"');

            // reserved names
            String[] names = {"con", "prn", "aux", "nul", "com1", "com2", "com3",
                "com4", "com5", "com6", "com7", "com8", "com9", "lpt1", "lpt2",
                "lpt3", "lpt4", "lpt5", "lpt6", "lpt7", "lpt8", "lpt9"};
            illegalNames.addAll(Arrays.asList(names));
        }
        
        ILLEGAL_CHARS = Collections.unmodifiableSet(illegalChars);
        ILLEGAL_NAMES = Collections.unmodifiableSet(illegalNames);
    }
    
    public static String sanitizeName(String name) {
        if (ILLEGAL_NAMES.contains(name.toLowerCase())) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder(name);
        for (int i = 0; i < sb.length(); i++) {
            if (ILLEGAL_CHARS.contains(sb.charAt(i))) {
                sb.deleteCharAt(i);
            }
        }
        return sb.toString();
    }
    
    public static boolean isValidName(String name) {
        if (ILLEGAL_NAMES.contains(name.toLowerCase())) {
            return false;
        }
        
        for (int i = 0; i < name.length(); i++) {
            if (ILLEGAL_CHARS.contains(name.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    private FilenameSanitizer() {
    }
}
