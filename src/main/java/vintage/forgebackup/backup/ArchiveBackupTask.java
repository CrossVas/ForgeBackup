package vintage.forgebackup.backup;

import java.util.TimerTask;

import net.minecraft.server.MinecraftServer;

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
