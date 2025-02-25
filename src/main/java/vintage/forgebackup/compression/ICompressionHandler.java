package vintage.forgebackup.compression;

import java.io.File;
import java.io.IOException;

public interface ICompressionHandler {
	void openFile(File backupFolder, String backupFilename) throws IOException;
	void addCompressedFile(File file) throws IOException;
	void closeFile() throws IOException;
	String getFileExtension();
	boolean isValidTargetDirectory(File directory);
	boolean isIncremental();
}
