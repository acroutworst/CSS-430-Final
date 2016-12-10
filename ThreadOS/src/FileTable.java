// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.util.Vector;

public class FileTable {

	private Vector<FileTableEntry> table;         // the actual entity of this file table
	private Directory dir;        // the root directory 
	private enum flag {
		READ,
		WRITE
	}
	private final int READ = 0;
	private final int WRITE = 1;

	public FileTable( Directory directory ) { // constructor
		table = new Vector( );     // instantiate a file (structure) table
		dir = directory;           // receive a reference to the Director
	}                             // from the file system

	// major public methods
	public synchronized FileTableEntry falloc( String filename, String mode ) {
		// allocate a new file (structure) table entry for this file name
		// allocate/retrieve and register the corresponding inode using dir
		// increment this inode's count
		// immediately write back this inode to the disk
		// return a reference to this file (structure) table entry
		
		short iNumber = -1;
		Inode inode = null;
		
		while(true) {
			iNumber = filename.equals("/") ? 0 : dir.namei(filename);
			
			if(iNumber >= 0) {
				inode = new Inode(iNumber);
				
				if(mode.equals("r")) {
					if(inode.flag == READ) {
						break;
					} else if(inode.flag == WRITE) {
						try { 
							wait(); 
						} catch (InterruptedException e) { }
					} else {
						iNumber = -1;
						return null;
					}
				} else if(mode.equals("w")) {
					// Cannot write after write
					if(inode.flag == READ) {
						break;
					} else {
						iNumber = -1;
						return null;
					}
				}
			}
		}
		
		inode.count++;
		inode.toDisk(iNumber);
		
		FileTableEntry entry = new FileTableEntry(inode, iNumber, mode);
		table.addElement(entry);
		
		return entry;
	}

	public synchronized boolean ffree( FileTableEntry e ) {
		// receive a file table entry reference
		// save the corresponding inode to the disk
		// free this file table entry.
		// return true if this file table entry found in my table
		
		e.inode.toDisk(e.iNumber);
		
		return table.remove(e);
		
//		return false;	// TODO: Actually write this
	}

	public synchronized boolean fempty( ) {
		return table.isEmpty( );  // return if table is empty 
	}                            // should be called before starting a format
}