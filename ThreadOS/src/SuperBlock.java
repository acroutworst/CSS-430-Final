class SuperBlock {
	public int totalBlocks; // the number of disk blocks
	public int totalInodes; // the number of inodes
	public int freeList;    // the block number of the free list's head
	private final int defaultInodeBlocks = 64;

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

}