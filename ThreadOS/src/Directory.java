// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.util.Arrays;

public class Directory {
	private static int maxChars = 30; 	  // max characters of each file name
	private static int dirBytes = 32; 	  // Root directory at 32 bytes of disk block 1
	
	// Directory entries
	private int fsize[];        		  // each element stores a different file size.
	private char fnames[][];    		  // each element stores a different file name.
	
	/**
	 * Constructor for Directory to initialize the file size, max files, root, file name, root name/size
	 * 
	 * @param maxInumber 
	 */
	public Directory( int maxInumber ) {  // directory constructor
		fsize = new int[maxInumber];      // maxInumber = max files
		for ( int i = 0; i < maxInumber; i++ ) 
			fsize[i] = 0;                 // all file size initialized to 0
		fnames = new char[maxInumber][maxChars];
		String root = "/";                // entry(inode) 0 is "/"
		fsize[0] = root.length( );        // fsize[0] is the size of "/".
		root.getChars( 0, fsize[0], fnames[0], 0 ); // fnames[0] includes "/"
	}

	/**
	 * directory2bytes - converts and returns Directory info into byte array;
	 * 					 byte array is then written back to disk
	 * @return
	 */
	public byte[] directory2bytes( ) {	
		
		int offset = 0;									// Account for offset								
		byte[] data = new byte[fsize.length*4 + fnames.length*(maxChars*2)]; // 60 bytes in Java for file name, 
																			 // added to file size * bytes 
		for(int i = 0; i < fsize.length; i++) {			
			SysLib.int2bytes(fsize[i], data, offset);	// Convert int to bytes (reversed of bytes2directory)
			offset += 4;								// Increment offset
		}
		
		offset = 0;										// Make sure offset is 0
		
		for(int i = 0; i < fsize.length; i++) {			// Traverse file
			String fname = new String(data, offset, maxChars*2); // Set file
			System.arraycopy(fname.getBytes(), 0, data, offset, fname.getBytes().length); // Perform array copy of file name
			offset += maxChars*2;						// Increment offset
		}
		
		return data;									// return dir info
	}
	
	/**
	 * Write entire directory file contents in bytes
	 * @param data
	 */
	public void bytes2directory(byte data[]) {
		int offset = 0;										// Account for offset
		
		for(int i = 0; i < fsize.length; i++) {				// Traverse through file 
			fsize[i] = SysLib.bytes2int(data, offset);		// Convert bytes to int
			offset += 4;									// Increment offset
		}
		
		for(int i = 0; i < fnames.length; i++) {			// Traverse through file
			String fname = new String(data, offset, maxChars*2); // Set file 
			fname.getChars(0, fsize[i], fnames[i], 0);		// Fill file name
			offset += maxChars*2;							// Increment offset
		}
	}

	/**
	 * filename is the one of a file to be created.
	 * allocates a new inode number for this filename
	 * 
	 * @param filename
	 * @return
	 */
	public short ialloc( String filename ) {
		short iNum = 0;								// iNumber		
		
		for ( ; iNum < fsize.length; iNum++)		// Traverse through file
		{
			if (fsize[iNum] == 0)					// If found,
			{
				fsize[iNum] = filename.length( );   // set filename length     
				filename.getChars( 0, fsize[iNum], fnames[iNum], 0 ); // copy filename contents
				return iNum;						// return the iNumber
			}
		}
		
		return -1;									// If not found, return error (which is -1)
	}
	
	/**
	 * deallocates this iNumber (inode number)
	 * the corresponding file will be deleted.
	 * 
	 * @param iNumber
	 * @return
	 */
	public boolean ifree( short iNumber ) {
		if (fsize[iNumber] > 0)						// If negative,
		{
			fsize[iNumber] = 0;						// set iNumber to 0, 
			Arrays.fill(fnames[iNumber], '\0'); 	// fill with 0's, 
			return true;							// and return true
		}
					
		return false;								// Return false otherwise
													// TODO: Actually write this method
	}

	/**
	 * returns the inumber corresponding to this filename
	 * 
	 * @param filename The name of the file to search for
	 * @return The ID of the Inode representing the file or -1 if not found.
	 */
	public short namei( String filename ) {
		int offset = 0;								 // Offset
		short iNum = 0;								 // iNumber 
		
		for(; iNum < fsize.length; iNum++) {		 // Traverse file
			if(filename.length() == fsize[iNum]) {   // If a match for size, 
				if(filename.equals(new String(fnames[iNum], offset, fsize[iNum]))) { // and if iNumber equal to filename,
					return iNum;					 // return iNumber element
				}
			}
		}
		
		return -1;										// Otherwise return error (-1)
	}
}