// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.util.*;

public class SysLib {
    public static int exec( String args[] ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.EXEC, 0, args );
    }

    public static int join( ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.WAIT, 0, null );
    }

    public static int boot( ) {
	return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.BOOT, 0, null );
    }

    public static int exit( ) {
	return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.EXIT, 0, null );
    }

    public static int sleep( int milliseconds ) {
	return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.SLEEP, milliseconds, null );
    }

    public static int disk( ) {
	return Kernel.interrupt( Kernel.INTERRUPT_DISK,
				 0, 0, null );
    }

    public static int cin( StringBuffer s ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.READ, 0, s );
    }

    public static int cout( String s ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.WRITE, 1, s );
    }

    public static int cerr( String s ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.WRITE, 2, s );
    }

    public static int rawread( int blkNumber, byte[] b ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.RAWREAD, blkNumber, b );
    }

    public static int rawwrite( int blkNumber, byte[] b ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.RAWWRITE, blkNumber, b );
    }

    public static int sync( ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.SYNC, 0, null );
    }

    public static int cread( int blkNumber, byte[] b ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.CREAD, blkNumber, b );
    }

    public static int cwrite( int blkNumber, byte[] b ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.CWRITE, blkNumber, b );
    }

    public static int flush( ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.CFLUSH, 0, null );
    }

    public static int csync( ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,
				 Kernel.CSYNC, 0, null );
    }
    
    /**
     * Called by SysLib.open in Test5 to invoke Kernel interruption -- OPEN
     * Opens a file and reads/writes/appends data depending on mode
     * 
     * @param filename The name of file to be used in Kernel
     * @param mode Read, Write, W+, Append
     * @return
     */
    public static int open( String filename, String mode ) {	
    	String[] args = new String[2];							// Create args for filename and mode
    	args[0] = filename;										// Filename is first argument
    	args[1] = mode;											// While mode is the second argument
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing args
				 Kernel.OPEN, 0, args );
    }
    
    /**
     * Called by SysLib.close in Test5 to invoke Kernel interruption -- CLOSE
     * Closes a file 
     * 
     * @param fd
     * @return
     */
    public static int close( int fd ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing file descriptor
				 Kernel.CLOSE, fd, null );
    }
    
    /**
     * Called by SysLib.read in Test5 to invoke Kernel interruption -- READ
     * Reads from a file given a File Descriptor and a set buffer 
     * 
     * @param fd
     * @param buffer
     * @return
     */
    public static int read( int fd, byte[] buffer ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing file descriptor
				 Kernel.READ, fd, buffer );						// and read from buffer
    }
    
    /**
     * Called by SysLib.write in Test5 to invoke Kernel interruption -- WRITE
     * Writes file given a File Descriptor and a set buffer 
     * 
     * @param fd
     * @param buffer
     * @return
     */
    public static int write( int fd, byte[] buffer) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing file descriptor
				 Kernel.WRITE, fd, buffer );					// and write to buffer
    }
    
    /**
     * Called by SysLib.format in Test5 to invoke Kernel interruption -- FORMAT
     * Formats file 
     * 
     * @param fd
     * @return
     */
    public static int format( int fd ) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing file descriptor
				 Kernel.FORMAT, fd, null );
    }
    
    /**
     * Called by SysLib.delete in Test5 to invoke Kernel interruption -- DELETE
     * Deletes file according to filename 
     * 
     * @param name
     * @return
     */
    public static int delete( String filename) {
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing file name
				 Kernel.DELETE, 0, filename );
    }
    
    /**
     * Called by SysLib.seek in Test5 to invoke Kernel interruption -- SEEK
     * Positions file according to seek function 
     * 
     * @param fd
     * @param filename
     * @param mode
     * @return
     */
    public static int seek( int fd, int filename, int mode) {
    	int[] args = new int[2];
    	args[0] = filename;
    	args[1] = mode;
        return Kernel.interrupt( Kernel.INTERRUPT_SOFTWARE,		// Call Kernel interruption, passing file descriptor,
				 Kernel.SEEK, fd, args);						// and passing args
    }

    public static String[] stringToArgs( String s ) {
	StringTokenizer token = new StringTokenizer( s," " );
	String[] progArgs = new String[ token.countTokens( ) ];
	for ( int i = 0; token.hasMoreTokens( ); i++ ) {
	    progArgs[i] = token.nextToken( );
	}
	return progArgs;
    }

    public static void short2bytes( short s, byte[] b, int offset ) {
	b[offset] = (byte)( s >> 8 );
	b[offset + 1] = (byte)s;
    }

    public static short bytes2short( byte[] b, int offset ) {
	short s = 0;
        s += b[offset] & 0xff;
	s <<= 8;
        s += b[offset + 1] & 0xff;
	return s;
    }

    public static void int2bytes( int i, byte[] b, int offset ) {
	b[offset] = (byte)( i >> 24 );
	b[offset + 1] = (byte)( i >> 16 );
	b[offset + 2] = (byte)( i >> 8 );
	b[offset + 3] = (byte)i;
    }

    public static int bytes2int( byte[] b, int offset ) {
	int n = ((b[offset] & 0xff) << 24) + ((b[offset+1] & 0xff) << 16) +
	        ((b[offset+2] & 0xff) << 8) + (b[offset+3] & 0xff);
	return n;
    }
}
