package vintage.forgebackup.compression;

import com.google.common.io.Files;
import net.minecraft.server.MinecraftServer;
import vintage.forgebackup.ForgeBackup;

import java.io.File;
import java.io.IOException;

public class FolderCompressionHandler extends CompressionHandler {
    File backupFolder;

    public FolderCompressionHandler(MinecraftServer server) {
        super(server);
    }

    @Override
    public void openFile(File backupFolder, String backupFilename) throws IOException {
        File backupFile = new File(backupFolder, backupFilename);
        if (!backupFile.mkdirs()) {
            throw new IOException(String.format("Unable to create new backup folder: %s", backupFile.getAbsolutePath()));
        }
        this.backupFolder = backupFile;
    }

    @Override
    public void addCompressedFile(File file) throws IOException {
        File target = new File(backupFolder, cleanPath(file.getCanonicalPath()));
        if (!file.isDirectory()) {
            Files.copy(file, target);
        } else {
            if (!target.mkdirs()) {
                ForgeBackup.LOGGER.warning(String.format("Unable to create folder %s. This backup will likely fail soon.", target.getAbsoluteFile()));
            }
        }
    }

    @Override
    public void closeFile() throws IOException {
        // We have nothing to close...
    }

    @Override
    public String getFileExtension() {
        return "";
    }

    @Override
    public boolean isValidTargetDirectory(File directory) {
        return true;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }
}
