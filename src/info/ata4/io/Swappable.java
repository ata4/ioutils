package info.ata4.io;

/*
 ** 2013 December 27
 **
 ** The author disclaims copyright to this source code.  In place of
 ** a legal notice, here is a blessing:
 **    May you do good and not evil.
 **    May you find forgiveness for yourself and forgive others.
 **    May you share freely, never taking more than you give.
 */


import java.nio.ByteOrder;

/**
 * Interface for IO classes that can dynamically swap the byte order.
 * 
 * @author Nico Bergemann <barracuda415 at yahoo.de>
 */
public interface Swappable {
    
    public ByteOrder order();

    public void order(ByteOrder order);
}
