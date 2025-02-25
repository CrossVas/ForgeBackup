package vintage.forgebackup.compression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import vintage.forgebackup.ForgeBackup;
import net.minecraft.server.MinecraftServer;

public class ZipCompressionHandler extends ArchiveCompressionHandler {
	protected ZipOutputStream backup;
	
	public ZipCompressionHandler(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public void openFile(File backupFolder, String backupFilename) throws IOException {
		if (backup != null) {
			throw new IOException("Cannot open a new backup while one is in progress using the same compression handler.");
		}
		backup = new ZipOutputStream(new FileOutputStream(new File(backupFolder, backupFilename)));
	}
	
	@Override
	public void addCompressedFile(File file) throws IOException {
		byte[] buffer = new byte[4096];
		int readBytes;
		
		if (file.isDirectory()) { return; }
		
		backup.putNextEntry(new ZipEntry(cleanPath(file.getCanonicalPath())));
		try {
			InputStream inputStream = new FileInputStream(file);
			while ((readBytes = inputStream.read(buffer)) >= 0) {
				backup.write(buffer, 0, readBytes);
			}
			inputStream.close();
		} catch (IOException e) {
			ForgeBackup.LOGGER.warning(String.format("Couldn't backup file: %s", file.getPath()));
		}
		backup.closeEntry();
	}
	
	@Override
	public void closeFile() throws IOException {
		backup.close();
		backup = null;
	}
	
	@Override
	public String getFileExtension() {
		return ".zip";
	}
}
