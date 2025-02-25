package vintage.forgebackup;

import java.util.Timer;
import java.util.logging.Logger;

import vintage.forgebackup.backup.ArchiveBackupTask;
import vintage.forgebackup.backup.BackupTask;
import vintage.forgebackup.command.CommandBackup;
import vintage.forgebackup.configuration.BackupConfiguration;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@SuppressWarnings("UnusedParameters")
@Mod(modid = "forgebackup", name = "ForgeBackup", useMetadata = true)
public class ForgeBackup implements ICommandSender {
	public static final Logger LOGGER = Logger.getLogger("forgebackup");

	private BackupConfiguration config;
	private Timer               backupTimer;
	private String              version;
	
	@Instance("forgebackup")
	private static ForgeBackup  instance;
	
	public static ForgeBackup instance() {
		return instance;
	}
	
	public BackupConfiguration config() {
		return config;
	}

	public String getVersion() {
		return version;
	}

	public ForgeBackup() {}

	@Mod.PreInit
	public void preInitialisation(FMLPreInitializationEvent event) {
		LOGGER.setParent(event.getModLog());
		if (event.getSide() == Side.SERVER) {
			// Only assign ourselves to the Minecraft logger if we're on the
			// server
			// If we do this in SSP, our logs will be completely hidden.
			LOGGER.setParent(FMLCommonHandler.instance().getMinecraftServerInstance().getLogAgent()
					.getServerLogger());
		}
		
		version = event.getModMetadata().version;
		config = new BackupConfiguration(event.getSuggestedConfigurationFile());
	}
	
	@Mod.Init
	public void initialisation(FMLInitializationEvent event) {
		LanguageRegistry.instance()
		        .addStringLocalization("ForgeBackup.backup.start", "en_US", "Starting a new backup.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.progress", "en_US",
		        "Creating new backup of your world...");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.folderExists", "en_US",
		        "Backup failed. Backup directory already exists and is not a directory.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.aborted", "en_US",
		        "Backup failed. Please check your server log for more information.");
		LanguageRegistry
		        .instance()
		        .addStringLocalization(
		                "ForgeBackup.backup.aborted",
		                "en_US",
		                "Backup folder is invalid. If you are doing an incremental backup then you must start your first one with an empty folder.");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.backup.complete", "en_US", "Backup complete!");
		
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.save.force", "en_US",
		        "Forcing an updated save...");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.save.disabled", "en_US", "Disabling saving...");
		LanguageRegistry.instance().addStringLocalization("ForgeBackup.save.enabled", "en_US", "Re-enabling saving...");
	}
	
	@Mod.PostInit
	public void postInitialisation(FMLPostInitializationEvent event) {}
	
	@Mod.ServerStarting
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBackup(event));
	}
	
	@Mod.ServerStarted
	public void serverStarted(FMLServerStartedEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		LOGGER.info(String.format("ForgeBackup starting for world: %s...", server.worldServers[0].getSaveHandler()
				.getWorldDirectoryName()));
		setupTimers(server);
	}
	
	@Mod.ServerStopping
	public void serverStopping(FMLServerStoppingEvent event) {
		backupTimer.cancel();
		LOGGER.info("ForgeBackup stopped.");
	}
	
	public void reloadConfiguration() {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		config.reload();
		backupTimer.cancel();
		setupTimers(server);
	}
	
	private void setupTimers(MinecraftServer server) {
		backupTimer = new Timer(true);
		backupTimer.scheduleAtFixedRate(new BackupTask(server), (long) config.getBackupInterval() * 60 * 1000,
                (long) config.getBackupInterval() * 60 * 1000);
		if (config().longtermBackupsEnabled()) {
			backupTimer.schedule(new ArchiveBackupTask(server), /* 30 seconds */30 * 1000);
		}
	}
	
	@Override
	public String getCommandSenderName() {
		return "Backup";
	}

	@Override
	public void sendChatToPlayer(String s) {
		LOGGER.info(String.format("Received message: %s", s));
	}

	@Override
	public boolean canCommandSenderUseCommand(int var1, String var2) {
		return true;
	}

	@Override
	public String translateString(String s, Object... objects) {
		return "";
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates(0, 0, 0);
	}
}
