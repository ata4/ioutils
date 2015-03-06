/*
 ** 2015 March 06
 **
 ** The author disclaims copyright to this source code. In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */
package info.ata4.io.buffer.source;

/**
 *
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public class NonReadableSourceException extends UnsupportedOperationException {

    /**
     * Creates a new instance of <code>NonReadableSourceException</code> without
     * detail message.
     */
    public NonReadableSourceException() {
    }

    /**
     * Constructs an instance of <code>NonReadableSourceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public NonReadableSourceException(String msg) {
        super(msg);
    }
}
