// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.util.Vector;

public class FileTable {

	private Vector<FileTableEntry> table;       // the actual entity of this file table
	
	private Vector<Inode> inodes;
	
	private Directory dir;        				// the root directory 
	private enum flag {
		READ,
		WRITE
	}
	private final int READ = 2;					// Read mode
	private final int WRITE = 3;				// Write mode
	private final int DELETE = 4;				// If needs to be deleted
	private final int USED = 0;					// If used
	private final int UNUSED = 1;				// If unused
	
	/**
	 * Instantiates the file structure table
	 * 
	 * @param directory
	 */
	public FileTable( Directory directory ) {   // constructor
		table = new Vector<FileTableEntry>( );  // instantiate a file (structure) table
		dir = directory;           				// receive a reference to the Director
		inodes = new Vector<Inode>();
	}                              				// from the file system

	/**
	 * allocate a new file (structure) table entry for this file name
	 * allocate/retrieve and register the corresponding inode using dir
	 * increment this inode's count
	 * immediately write back this inode to the disk
	 * return a reference to this file (structure) table entry
	 * 
	 * @param filename The name of the file to search for
	 * @param mode Read, Write, W+, or Append
	 * @return
	 */
	public synchronized FileTableEntry falloc( String filename, String mode ) {

		short iNumber = -1;						// iNumber									
		Inode inode = null;						// Declared Inode
		boolean allModesNoRead = (mode.equals("w") || mode.equals("w+") || mode.equals("a")); // All modes except READ 

		while(true) {
			iNumber = filename.equals("/") ? 0 : dir.namei(filename); // determines filename

		if(iNumber >= 0) {						// If valid,																				
				//inode = new Inode(iNumber);		// make new Inode
				if (iNumber >= inodes.size())
				{
					// We should have already created it, but there is that situation where root is created
					//	before it gets an Inode assigned
					inodes.add(new Inode(iNumber));
				}
				inode = inodes.get(iNumber);
			
			
				if(mode.equals("r")) {			// If mode is Read,
					if(inode.flag == READ || inode.flag == USED || inode.flag == UNUSED ) { // and if not write,
						break;					// no need to wait
					} else if(inode.flag == WRITE) { // If Write,
						try { 					// wait for a write to exit
							wait(); 
						} catch (InterruptedException e) { }
					} else {					// no more open
						iNumber = -1;
						return null;
					}
				} else {						// If not Read,							
					if(inode.flag == USED || inode.flag == UNUSED || inode.flag == WRITE) { // and if not read,
						break;					// no need to wait
					} else {
						iNumber = -1;			// no more open
						return null;
					}
				}
			} else { 							// No file was found 
				if(allModesNoRead) {			// and no Read
					iNumber = dir.ialloc(filename);	// allocate/retrieve and register the corresponding inode using dir 
					
					inode = new Inode(iNumber);
					inodes.add(inode);
					
					break;	
				} else if(!allModesNoRead) {	// If Read,
					return null;				// return null reference
				} 
			}
		}

		inode.count++;							// Increment Inode's count
		inode.toDisk(iNumber);					// Immediately write back this inode to the disk

		FileTableEntry entry = new FileTableEntry(this, iNumber, mode);
		table.addElement(entry);				// Return a reference to this file (structure) table entry

		return entry;
	}

	/**
	 * receive a file table entry reference 
	 * save the corresponding inode to the disk
	 * free this file table entry 
	 * return true if this file table entry found in my table
	 * 
	 * @param e The file table entry that is to be freed
	 * @return
	 */
	public synchronized boolean ffree( FileTableEntry e ) {
		
		//Inode inode = new Inode(e.iNumber); // Receive file table entry reference and 
		Inode inode = inodes.get(e.iNumber);
		
		
		
		inode.count--;						// Decrement Inode's count
		inode.toDisk(e.iNumber);			// Save Inode to the disk 
		
		return table.remove(e);				// Free the entry and return if it's found
	}

	/**
	 * Determine if table is empty
	 * 
	 * @return
	 */
	public synchronized boolean fempty( ) {
		return table.isEmpty( );  // return if table is empty 
	}                             // should be called before starting a format
	
	public synchronized Inode retrieveInode(FileTableEntry entry)
	{
		return inodes.get(entry.iNumber);
	}
	public synchronized Inode retrieveInode(int iNumber)
	{
		return inodes.get(iNumber);
	}
}