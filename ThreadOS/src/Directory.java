// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.util.Arrays;

public class Directory {
	private static int maxChars = 30; // max characters of each file name

	// Directory entries
	private int fsize[];        // each element stores a different file size.
	private char fnames[][];    // each element stores a different file name.
	
	public Directory( int maxInumber ) { // directory constructor
		fsize = new int[maxInumber];     // maxInumber = max files
		for ( int i = 0; i < maxInumber; i++ ) 
			fsize[i] = 0;                 // all file size initialized to 0
		fnames = new char[maxInumber][maxChars];
		String root = "/";                // entry(inode) 0 is "/"
		fsize[0] = root.length( );        // fsize[0] is the size of "/".
		root.getChars( 0, fsize[0], fnames[0], 0 ); // fnames[0] includes "/"
	}

	public byte[] directory2bytes( ) {
		// converts and return Directory information into a plain byte array
		// this byte array will be written back to disk
		// note: only meaningfull directory information should be converted
		// into bytes.	
		
		int offset = 0;
		byte[] data = new byte[fsize.length];
		
		
		for(int i = 0; i < fsize.length; i++) {
			SysLib.int2bytes(fsize[i], data, offset);
		}
		
		for(int i = 0; i < fsize.length; i++) {
			String fname = new String(data, offset, maxChars*2);
			System.arraycopy(fname.getBytes(), 0, data, offset, fname.getBytes().length);
		}
		
		return null;
	}
	
	/**
	 * Write entire directory file contents in bytes
	 * @param data
	 */
	public void bytes2directory(byte data[]) {
		int offset = 0;
		
		for(int i = 0; i < fsize.length; i++) {
			fsize[i] = SysLib.bytes2int(data, offset);
		}
		
		for(int i = 0; i < fnames.length; i++) {
			String fname = new String(data, offset, maxChars*2);
			fname.getChars(0, fsize[i], fnames[i], 0);
		}
	}

	public short ialloc( String filename ) {
		// filename is the one of a file to be created.
		// allocates a new inode number for this filename
		
		return -1;
	}

	public boolean ifree( short iNumber ) {
		// deallocates this inumber (inode number)
		// the corresponding file will be deleted.
		
		return false;	// TODO: Actually write this method
	}

	/**
	 * 
	 * @param filename The name of the file to search for
	 * @return The ID of the Inode representing the file or -1 if not found.
	 */
	public short namei( String filename ) {
		// returns the inumber corresponding to this filename
		char[] name = filename.toCharArray();
		short index = 0;
		for ( ; index < fnames.length; index++)
		{
			if (Arrays.equals(name, fnames[index]))
			{
				return index;
			}
		}
		return -1;
	}
}