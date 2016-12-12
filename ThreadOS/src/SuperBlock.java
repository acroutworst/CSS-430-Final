// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

class SuperBlock {
	public int totalBlocks; // the number of disk blocks
	public int totalInodes; // the number of inodes
	public int freeList;    // the block number of the free list's head
	private final int defaultInodeBlocks = 64; // Default total Inode blocks
	public int inodeBlocks;
	private final int defaultBlocks = 1000;

	/**
	 * Creates the first disk block, block 0, the SuperBlock
	 * 
	 * @param diskSize The disk size of the superblock to compare to total blocks
	 */
	public SuperBlock( int diskSize ) {

		byte[] blockData = new byte[Disk.blockSize];	// Set block data size
		SysLib.rawread(0, blockData);					// Read block data
		
		totalBlocks = SysLib.bytes2int(blockData, 0);	// Convert bytes to int
		totalInodes = SysLib.bytes2int(blockData, 4);	// Convert bytes to int with offset of 4 
		
		inodeBlocks = totalInodes;		// TODO: Might not be entirely accurate, maybe changes based on how many files there are currently
		
		freeList = SysLib.bytes2int(blockData, 8);		// Convert bytes to int with offset of 8
		
		if((totalBlocks == diskSize) 					// If total size and disk size is a match,	
			&& (totalInodes >= 0) 						// and there is an Inode,
			&& freeList >= 0) { 						// and there is a list of free blocks,
			return;										// return
		} else {										
			totalBlocks = diskSize;						// Otherwise, set disk size to total blocks
			format(defaultInodeBlocks);					// and format disk 
		}
	}
	
	/**
	 * Resets the disk and reassigns the disk space to the params size
	 * 
	 * @param blocks
	 */
	
	// CHANGE THIS METHOD
	public void format(int blocks) {
				
		totalInodes = blocks;
		inodeBlocks = totalInodes;
		
		int iNodesPerBlock = Disk.blockSize / 32;
		
		int numINodeStorageBlocks = (int) Math.ceil(blocks / iNodesPerBlock);
		
		this.freeList = numINodeStorageBlocks + 1;
		
		byte[] data = new byte[Disk.blockSize];
		
		SysLib.int2bytes(freeList+1, data, 0);
		SysLib.rawwrite(freeList, data);
		
		for (int iter = freeList; iter < totalBlocks ; iter++)
		{
			
			SysLib.int2bytes(iter + 1, data, 0);
			SysLib.rawwrite(iter, data);
		}
		
		sync();
	}
	
	
	/**
	 * Write back totalBlocks, inodesBlock, and freeList to disk 
	 */
	public void sync() {
		byte[] blockData = new byte[Disk.blockSize];		// Set block data size
		
		SysLib.int2bytes(totalBlocks, blockData, 0);		// Convert int to bytes 
		SysLib.int2bytes(totalInodes, blockData, 4);		// Convert int to bytes with offset of 4
		SysLib.int2bytes(freeList, blockData, 8);			// Convert int to bytes with offset of 8
		
		SysLib.rawwrite(0, blockData);						// Write block back
	}
	
	/**
	 * Dequeue the top block from the free list
	 */
	public int getFreeBlock() {
		
		byte[] data = new byte[Disk.blockSize];				// Set block data size			
		
		SysLib.rawread(freeList, data);						// Read free list of blocks
		
		int result = SysLib.bytes2int(data, 0);				// Convert bytes to int
		
		int tmp = freeList;									// Save free list
		freeList = result;									// Assign converted int to free list 
			
		return tmp;											// and return list of free blocks
	}

	/**
	 * Enqueue a given block to the front of the free list
	 * @param blockNumber
	 */
	public void returnBlock(int blockNumber) {
		
		byte[] data = new byte[Disk.blockSize];				// Set block data size
		SysLib.int2bytes(freeList, data, 0);				// Convert int to bytes 
		
		SysLib.rawwrite(blockNumber, data);					// Write block data
		
		freeList = blockNumber;								// Return block number 
	}
}