package vintage.forgebackup.backup;

import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import vintage.forgebackup.ForgeBackup;
import vintage.forgebackup.compression.ICompressionHandler;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public class BackupSettings {
    public static final Pattern absolutePattern = Pattern.compile("^(([A-Za-z]:|\\\\)?\\\\|[\\\\/])[^\\\\/]");
    private final IBackupCleanup cleanup;
    private final String backupFolder;
    private final boolean backupWorld;
    private final boolean backupConfiguration;
    private final boolean backupMods;
    private final boolean backupServerConfiguration;
    private final String[] extraFiles;
    private final int[] disabledDimensions;
    private final int loggingLevel;
    private final MinecraftServer server;
    private final ICompressionHandler compression;

    public BackupSettings(
            MinecraftServer server, String backupFolder, int loggingLevel, IBackupCleanup cleanup,
            boolean backupWorld, boolean backupConfiguration, boolean backupMods, boolean backupServerConfiguration, String[] extraFiles,
            int[] disabledDimensions, ICompressionHandler compression
    ) {
        this.server = server;
        this.backupFolder = backupFolder;
        this.loggingLevel = loggingLevel;
        this.cleanup = cleanup;
        this.backupWorld = backupWorld;
        this.backupConfiguration = backupConfiguration;
        this.backupMods = backupMods;
        this.backupServerConfiguration = backupServerConfiguration;
        this.extraFiles = extraFiles;
        this.disabledDimensions = disabledDimensions;
        this.compression = compression;
    }

    public IBackupCleanup getBackupCleanupHandler() {
        return cleanup;
    }

    public boolean willBackupWorld() {
        return backupWorld;
    }

    public boolean willBackupConfiguration() {
        return backupConfiguration;
    }

    public boolean willBackupMods() {
        return backupMods;
    }

    public boolean willBackupServerConfiguration() {
        return backupServerConfiguration;
    }

    public List<File> getExtraFilesToBackup(MinecraftServer server) {
        List<File> files = Lists.newArrayList();
        for (String file : extraFiles) {
            files.add(server.getFile(file));
        }
        return files;
    }

    public List<Integer> getDisabledDimensions() {
        List<Integer> dimensions = Lists.newArrayList();

        for (int d : disabledDimensions) {
            if (d == 0) {
                ForgeBackup.LOGGER.warning("Cannot disable overworld from backups.");
                continue;
            }
            dimensions.add(d);
        }

        return dimensions;
    }

    public int getLoggingLevel() {
        return loggingLevel;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ICompressionHandler getCompressionHandler() {
        return compression;
    }

    public File getBackupFolder() {
        File folder = absolutePattern.matcher(backupFolder).find() ? new File(backupFolder) : server.getFile(backupFolder);
        ISaveHandler saveHandler = server.worldServers[0].getSaveHandler();
        return new File(folder, saveHandler.getWorldDirectoryName());
    }

    public String getBackupFileName() {
        return getBackupCleanupHandler().getBackupFilename() + getCompressionHandler().getFileExtension();
    }

    public File getBackupFile() {
        return new File(getBackupFolder(), getBackupFileName());
    }

    public List<File> getFilesToBackup() {
        ISaveHandler saveHandler = server.worldServers[0].getSaveHandler();
        List<File> thingsToSave = Lists.newArrayList(getExtraFilesToBackup(server));

        if (willBackupWorld()) {
            if (saveHandler instanceof SaveHandler) {
                thingsToSave.add(((SaveHandler) saveHandler).getWorldDirectory());
            } else {
                thingsToSave.add(server.getFile(saveHandler.getWorldDirectoryName()));
            }
        }

        if (willBackupConfiguration()) {
            thingsToSave.add(server.getFile("config"));
        }

        if (willBackupMods()) {
            thingsToSave.add(server.getFile("mods"));
            thingsToSave.add(server.getFile("coremods"));
        }

        if (willBackupServerConfiguration()) {
            thingsToSave.add(server.getFile("banned-ips.txt"));
            thingsToSave.add(server.getFile("banned-players.txt"));
            thingsToSave.add(server.getFile("ops.txt"));
            thingsToSave.add(server.getFile("server.properties"));
            thingsToSave.add(server.getFile("white-list.txt"));
        }

        return thingsToSave;
    }
}
