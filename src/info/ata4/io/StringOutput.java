/*
 ** 2015 February 20
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface StringOutput {

    void writeStringFixed(String str, Charset charset) throws IOException;

    void writeStringFixed(String str) throws IOException;

    void writeStringNull(String str, Charset charset) throws IOException;

    void writeStringNull(String str) throws IOException;

    void writeStringPrefixed(String str, Class<? extends Number> prefixType, Charset charset) throws IOException;

    void writeStringPrefixed(String str, Class<? extends Number> prefixType) throws IOException;
    
}
