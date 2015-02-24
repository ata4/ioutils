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
public interface StringInput {

    /**
     * Reads an unterminated, fixed length string. If the string contains one or
     * more null characters, the string is truncated to the position before the
     * first null character.
     *
     * The character set must encode each character in one byte, the behavior for
     * multi-byte character sets is not defined.
     *
     * @param charset character set to use when converting the byte array to string
     * @param length total length of the string in bytes
     * @return a string encoded as <code>charset</code>
     * @throws IOException
     */
    String readStringFixed(int length, Charset charset) throws IOException;

    String readStringFixed(int length) throws IOException;

    /**
     * Reads a null-terminated C-style string with variable length.
     *
     * The character set must encode each character in one byte, the behavior for
     * multi-byte character sets is not defined.
     *
     * @param limit maximum allowed length of the resulting string
     * @param charset character set to use when converting the byte array to string
     * @return
     * @throws IOException
     */
    String readStringNull(int limit, Charset charset) throws IOException;

    /**
     * Reads a null-terminated C-style string with variable length using the
     * ASCII character set.
     *
     * @param limit maximum allowed length of the resulting string
     * @return
     * @throws IOException
     */
    String readStringNull(int limit) throws IOException;

    /**
     * Reads a null-terminated C-style string with variable length using the
     * ASCII character set and a limit of 256 characters.
     *
     * @return
     * @throws IOException
     */
    String readStringNull() throws IOException;

    /**
     * Reads a length-prefixed Pascal-style string.
     *
     * The allowed types for the prefix in <code>charset</code> are:
     * Byte.TYPE, Short.TYPE or Integer.TYPE.
     *
     * If the prefix is longer than <code>limit</code>, the remaining bytes are
     * simply skipped.
     *
     * The character set must encode each character in one byte, the behavior for
     * multi-byte character sets is not defined.
     *
     * @param <T> number type class
     * @param prefixType number type for the length
     * @param limit maximum allowed length of the resulting string
     * @param charset character set to use when converting the byte array to string
     * @return
     * @throws IOException
     */
    <T extends Number> String readStringPrefixed(Class<T> prefixType, T limit, Charset charset) throws IOException;

    /**
     * Reads a length-prefixed Pascal-style string using the ASCII character set.
     *
     * The allowed types for the prefix in <code>charset</code> are:
     * Byte.TYPE, Short.TYPE or Integer.TYPE.
     *
     * If the prefix is longer than <code>limit</code>, the remaining bytes are
     * simply skipped.
     *
     * @param <T> number type class
     * @param prefixType number type for the length
     * @param limit maximum allowed length of the resulting string
     * @return
     * @throws IOException
     */
    <T extends Number> String readStringPrefixed(Class<T> prefixType, T limit) throws IOException;
    
}
