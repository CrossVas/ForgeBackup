package vintage.forgebackup.backup;

import java.io.File;
import java.util.TimerTask;

import vintage.forgebackup.ForgeBackup;
import vintage.forgebackup.configuration.BackupConfiguration;
import net.minecraft.server.MinecraftServer;

public class BackupTask extends TimerTask {
	private final MinecraftServer server;
	private static boolean lastRunHadPlayers = false; 
	
	public BackupTask(MinecraftServer server) {
		this.server = server;
	}

	@Override
	public void run() {
		processArchiveBackups(server);
		processRegularBackups(server);
	}

	public static void processArchiveBackups(MinecraftServer server) {
		BackupConfiguration config = ForgeBackup.instance().config();
		if (!config.longtermBackupsEnabled())
		{ return; }
		
		BackupSettings archiveSettings = config.getArchiveBackupSettings(server);
		File archiveBackup = archiveSettings.getBackupFile();
		
		if (archiveBackup.exists())
		{ return; }
		
		Backup backup = new Backup(archiveSettings);
		backup.run(ForgeBackup.instance());
	}

	public static void processRegularBackups(MinecraftServer server) {
		BackupConfiguration config = ForgeBackup.instance().config();
		if (config.onlyRunBackupsWithPlayersOnline() && !playerLoggedIn(server)) {
			boolean realLastRun = lastRunHadPlayers;
			lastRunHadPlayers = false;
			
			if (!realLastRun) { return; }
		} else {
			lastRunHadPlayers = true;
		}
		
		Backup backup = new Backup(config.getRegularBackupSettings(server));
		backup.run(ForgeBackup.instance());
	}
	
	public static boolean playerLoggedIn(MinecraftServer server) {
		return !server.getConfigurationManager().playerEntityList.isEmpty();
	}
}
