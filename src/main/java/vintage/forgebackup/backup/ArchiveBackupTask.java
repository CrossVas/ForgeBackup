package vintage.forgebackup.backup;

import net.minecraft.server.MinecraftServer;

import java.util.TimerTask;

public class ArchiveBackupTask extends TimerTask {
    private final MinecraftServer server;

    public ArchiveBackupTask(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        BackupTask.processArchiveBackups(server);
    }
}
