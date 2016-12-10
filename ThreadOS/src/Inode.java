// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

import java.nio.ByteBuffer;

public class Inode {
	private final static int iNodeSize = 32;       // fix to 32 bytes
	private final static int directSize = 11;      // # direct pointers
	
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
	 * 
	 * @param iNumber The ID of the Inode to pull from the Disk.
	 */
	Inode( short iNumber ) {                       // retrieving inode from disk
		// design it by yourself.
		byte[] blockData = new byte[Disk.blockSize];
		
		int blockNumber = iNodeStorageBlock + iNumber / numINodesPerBlock;
		
		SysLib.rawread(blockNumber, blockData);
		ByteBuffer builder = ByteBuffer.allocate(iNodeSize);
		builder.put(blockData, iNodeSize * (iNumber % numINodesPerBlock), iNodeSize);
		
		builder.rewind();
		
		length = builder.getInt();
		count = builder.getShort();
		flag = builder.getShort();
		
		for (int i = 0; i < direct.length; i++)
		{
			direct[i] = builder.getShort();
		}
		
		indirect = builder.getShort();
	}

	/**
	 * Saves the Inode to the disk.
	 * 
	 * @param iNumber The ID of the Inode to be written to the disk
	 * @return The result of the operation. -1 for failure and 0 for success.
	 */
	synchronized int toDisk( short iNumber ) {                  // save to disk as the i-th inode
		
		byte[] blockData = new byte[Disk.blockSize];
		
		ByteBuffer aggregator = ByteBuffer.allocate(iNodeSize);
		aggregator.putInt(length);
		aggregator.putShort(count);
		aggregator.putShort(flag);
		
		for (short sh : direct)
			aggregator.putShort(sh);
		
		aggregator.putShort(indirect);
		
		int blockNumber = iNodeStorageBlock + iNumber / numINodesPerBlock;
		
		SysLib.rawread(blockNumber, blockData);
		
//		int arrayIndex = iNodeSize * iNumber;
		int arrayIndex = iNodeSize * (iNumber % numINodesPerBlock);
		
		System.arraycopy(aggregator.array(), 0, blockData, arrayIndex, iNodeSize);
		
		SysLib.rawwrite(blockNumber, blockData);
		
		// TODO: Look into when the operations can fail, particularly the read and write operations
		return 0;
	}
	
	/**
	 * 
	 * @return The index block for this Inode
	 */
	public short getIndexBlockNumber() {
		return indirect;
	}
	
	/**
	 * 
	 * @param indexBlockNumber The block to use as the index block for this Inode
	 * @return true when the passed block number is accepted as the index block
	 */
	public boolean setIndexBlock(short indexBlockNumber) {
		if (indirect != -1)
		{
			indirect = indexBlockNumber;
			return true;
		}
		return false;
	}
	
	/**
	 * Determine the block that is home to the data existing at offset
	 * 
	 * @param offset The offset into the File
	 * @return The block that the data exists in
	 */
	public short findTargetBlock(int offset) {
		short blk = (short)(offset / Disk.blockSize);
		
		if (blk < direct.length)
		{
			return blk;
		}
		blk -= direct.length;
		
		byte[] data = new byte[Disk.blockSize];
		SysLib.rawread(indirect,data);
		
		return SysLib.bytes2short(data, (blk * 2));
	}
}