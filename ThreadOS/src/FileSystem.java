// Adam Croutworst and Logan McArthur
// CSS 430
// File System Project
// December 12, 2016

// source: http://courses.washington.edu/css430/prog/CSS430FinalProject.pdf

public class FileSystem {
	private SuperBlock superblock;
	private Directory dir;
	private FileTable filetable;
	
	public FileSystem(int diskBlocks) {
		superblock = new SuperBlock(diskBlocks);
		dir = new Directory(superblock.inodeBlocks);
		filetable = new FileTable(dir);
		
		FileTableEntry entry = 	open("/", "r");
		
		int dirSize = fsize(entry);
		
		if(dirSize > 0 ) {
			byte[] dirData = new byte[dirSize];
			read(entry, dirData);
			dir.bytes2directory(dirData);
		}
		
		close(entry);
	}
	
	void sync() {
		
	}
	
	boolean format(int files) {
		return false;
	}
	
	FileTableEntry open(String filename, String mode) {
		FileTableEntry fEnt = filetable.falloc(filename, mode);
		
		if(mode.equals("w")) {
			if(deallocAllBlocks(fEnt) == false) {
				return null;
			}
		}
		
		return fEnt;
	}
	
	boolean close(FileTableEntry fEnt) {
		return false;
	}
	
	int fsize(FileTableEntry fEnt) {
		return 0;
	}
	
	int read(FileTableEntry fEnt, byte[] buffer) {
		return 0;
	}
	
	int write(FileTableEntry fEnt, byte[] buffer) {
		return 0;
	}
	
	private boolean deallocAllBlocks(FileTableEntry fEnt) {
		return false;
	}
	
	boolean delete(String filename) {
		return false;
	}
	
	private final int SEEK_SET = 0;
	private final int SEEK_CUR = 1;
	private final int SEEK_END = 2;
	
	int seek(FileTableEntry fEnt, int offset, int whence) {
		return 0;
	}

	
}
