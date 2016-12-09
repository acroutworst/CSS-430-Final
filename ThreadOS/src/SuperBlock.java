// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

class SuperBlock {
	public int totalBlocks; // the number of disk blocks
	public int totalInodes; // the number of inodes
	public int freeList;    // the block number of the free list's head
	private final int defaultInodeBlocks = 64;
	public int inodeBlocks;

	/**
	 * Creates the first disk block, block 0, the SuperBlock
	 * 
	 * @param diskSize The disk size of the superblock to compare to total blocks
	 */
	public SuperBlock( int diskSize ) {
		SysLib.cout("SuperBlock constructor: " + diskSize);
		
		byte[] blockData = new byte[Disk.blockSize];
		SysLib.rawread(0, blockData);
		
		totalBlocks = SysLib.bytes2int(blockData, 0);
		totalInodes = SysLib.bytes2int(blockData, 4);
		freeList = SysLib.bytes2int(blockData, 8);
		
		if((totalBlocks == diskSize) 
			&& (totalInodes >= 0) 
			&& freeList >= 0) { 
			return;
		} else {
			totalBlocks = diskSize;
			format(defaultInodeBlocks);
		}
	}
	
	/**
	 * Resets the disk and reassigns the disk space to the params size
	 * 
	 * @param blocks
	 */
	public void format(int blocks) {
		
	}
	
	/**
	 * Write back totalBlocks, inodesBlock, and freeList to disk 
	 */
	public void sync() {
		
	}
	
	/**
	 * Dequeue the top block from the free list
	 */
	public void getFreeBlock() {
		
	}

	/**
	 * Enqueue a given block to the end of the free list
	 * @param blockNumber
	 */
	public void returnBlock(int blockNumber) {
		
	}
}