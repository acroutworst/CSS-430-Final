// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.nio.ByteBuffer;

public class Inode {
	private final static int iNodeSize = 32;       // fix to 32 bytes
	public final static int directSize = 11;       // # direct pointers
	
	private final static int iNodeStorageBlock = 1;	// Location that Inodes are stored on the disk
	private final static int numINodesPerBlock = Disk.blockSize / iNodeSize;
	
	public int length;                             // file size in bytes
	public short count;                            // # file-table entries pointing to this
	public short flag;                             // 0 = unused, 1 = used, ...
	public short direct[] = new short[directSize]; // direct pointers
	public short indirect;                         // a indirect pointer

	/**
	 * Creates an Inode with the default values
	 */
	Inode( ) {                                     // a default constructor
		length = 0;
		count = 0;
		// TODO: Look into why the default Inodes would have the flag set to Used
		flag = 1;
		for ( int i = 0; i < directSize; i++ )
			direct[i] = -1;
		indirect = -1;
	}

	/**
	 * Inode constructor - Defines the contents of the Inode 
	 * 
	 * @param iNumber The ID of the Inode to pull from the Disk.
	 */
	Inode( short iNumber ) {                       // retrieving inode from disk

		byte[] blockData = new byte[Disk.blockSize];			// Set the block data
		
		int blockNumber = iNodeStorageBlock + iNumber / numINodesPerBlock; // Get block number
		
		SysLib.rawread(blockNumber, blockData);					// Read data block
		ByteBuffer builder = ByteBuffer.allocate(iNodeSize);	// Allocate space for 32 bytes (total Inode data)
		builder.put(blockData, iNodeSize * (iNumber % numINodesPerBlock), iNodeSize); // Fill buffer with array info
		
		builder.rewind();										// Rewinds buffer position
		
		length = builder.getInt();								// Reads next 4 bytes (int)
		count = builder.getShort();								// Reads next 2 bytes (short)
		flag = builder.getShort();								// Reads next 2 bytes (short)
		
		for (int i = 0; i < direct.length; i++)					// Traverse through direct block,
		{
			direct[i] = builder.getShort();						// and get next 2 bytes (short) of each direct block
		}
		
		indirect = builder.getShort();							// Reads last 2 bytes (short) for indirect block
	}

	/**
	 * Saves the Inode to the disk.
	 * 
	 * @param iNumber The ID of the Inode to be written to the disk
	 * @return The result of the operation. -1 for failure and 0 for success.
	 */
	synchronized int toDisk( short iNumber ) {                  // save to disk as the i-th inode
		
		byte[] blockData = new byte[Disk.blockSize];			// Get block data	
		
		ByteBuffer aggregator = ByteBuffer.allocate(iNodeSize); // Assign buffer length
		aggregator.putInt(length);								// Get next 4 bytes (int)	
		aggregator.putShort(count);								// Reads next 2 bytes (short)
		aggregator.putShort(flag);								// Reads next 2 bytes (short)
		
		for (short sh : direct)									// Traverse direct blocks
			aggregator.putShort(sh);							// Read next 2 bytes (short) for each block
		
		aggregator.putShort(indirect);							// Get last 2 bytes (short) for indirect block
		
		int blockNumber = iNodeStorageBlock + iNumber / numINodesPerBlock; // Get Block number
		
		SysLib.rawread(blockNumber, blockData);					// Read block info
		
		int arrayIndex = iNodeSize * (iNumber % numINodesPerBlock);	// Get array index
		
		System.arraycopy(aggregator.array(), 0, blockData, arrayIndex, iNodeSize); // Copy array from buffer 
																				   // to index in block
		SysLib.rawwrite(blockNumber, blockData);				// Write the block			
		
		// TODO: Look into when the operations can fail, particularly the read and write operations
		return 0;												// Return valid (wrote to disk)
	}
	
	/**
	 * 
	 * CHANGE THIS METHOD
	 * Get index block for this Inode
	 * 
	 * @return The index block for this Inode
	 */
	public short getIndexBlockNumber() {
		return indirect;
	}
	
	/**
	 * Set the IndexBlock based on Direct or Indirect
	 * 
	 * @param indexBlockNumber The block to use as the index block for this Inode
	 * @return true when the passed block number is accepted as the index block
	 */
	public boolean setIndexBlock(short indexBlockNumber) {
		
		if(indirect == -1) {								// If indirect,
			indirect = indexBlockNumber;					// Set index block to indirect
			byte[] data = new byte[Disk.blockSize];			// Set block data size
			
			for(int i = 0; i < (Disk.blockSize / 2); i++) {	// Traverse through block
				short offset = (short) (i);					// Get offset
				SysLib.short2bytes(indirect, data, offset); // Convert short to bytes
			}
			
			SysLib.rawwrite(indexBlockNumber, data);		// Write block info
			
			return true;									// If everything went well, return true
		}
		
		return false;										// Otherwise, return false
	}
	
	/**
	 * Determine the block that is home to the data existing at offset
	 * 
	 * @param offset The offset into the File
	 * @return The block that the data exists in
	 */
	public int findTargetBlock(int offset) {
		
        short blk = (short)(offset / Disk.blockSize);		// Create and save Block at offset
        byte[] data = new byte[Disk.blockSize];				// Create block size
        
        if(blk < direct.length) {							// If block is direct,
        	return direct[blk];								// return direct block
        }
        
        if(indirect < 0) {									// If indirect,
        	return -1;										// return -1 (indirect)
        }
        
        blk -= direct.length;								// Decrement block count
        
        SysLib.rawread(indirect, data);						// Read indirect block data
        
        return SysLib.bytes2short(data, (blk * 2));			// Convert bytes to short				
	}
}