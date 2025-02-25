package vintage.forgebackup.configuration;

import java.io.File;

import vintage.forgebackup.ForgeBackup;
import vintage.forgebackup.backup.ArchiveBackupCleanup;
import vintage.forgebackup.backup.BackupSettings;
import vintage.forgebackup.backup.RegularBackupCleanup;
import vintage.forgebackup.compression.CompressionType;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

@SuppressWarnings("CanBeFinal")
public class BackupConfiguration extends MonoConfiguration {
	private static final String BACKUP_SECTION = "backup";
	private static final String LONGTERM_BACKUP_SECTION = "backup.longterm";

	////////////////////////////////////////////////////////
	//                   GENERAL                          //
	////////////////////////////////////////////////////////
	@Section(section = Section.GENERAL, comment = "General configuration options are here")
	protected ConfigCategory general;
	
	@Option(comment = "Interval in minutes between automatic backup attempts.")
	protected int backupInterval = 15;
	
	@Option(
		comment =
			"Maximum backups to keep stored. Older backups will be deleted first.\n" +
			"-1 will disable automated cleanups and no backups will ever be deleted.\n" +
			"This option has no effect if you are using the git compression type."
	)
	protected int maxBackups = -1;
	
	@Option(comment = "If this is set to true, then only operators may manually run backups with `/backup run`.")
	protected boolean opsOnly = true;
	
	@Option(comment = "If this is set to true, then command blocks can be used with all `/backup` commands.")
	protected boolean commandBlocksAllowed = false;
	
	@Option(
		comment =
			"Only run automated backups when there is a player connected to the\n" +
			"server. This option has no effect in single player. Long-term backups\n" +
			"will always run whether there are players connected or not."
	)
	protected boolean backupOnlyWithPlayer = true;
	
	@Option(comment = "How much information to send to players while backing up.\n\n" +
			"0 = nothing\n" +
			"1 = normal\n" +
			"2 = debugging."
	)
	protected int loggingLevel = 1;
	
	@Option(
		comment =
			"If this is set to true, then ForgeBackup will check online for updates.\n" +
			"The update notification will only be sent to the console."
	)
	protected boolean checkUpdates = false;
	
	////////////////////////////////////////////////////////
	//                   BACKUP                           //
	////////////////////////////////////////////////////////
	@Section(section = BACKUP_SECTION, comment = "These settings control what things are backed up and how.")
	protected ConfigCategory backup;
	
	@Option(section = BACKUP_SECTION,
		comment =
			"Folder name to store backups in. Each world's backups will be stored in\n" +
			"subfolders of this one. This can be an absolute path.\n\n" +
			"Examples:\n" +
			"- backups\n" +
			"- C:\\backups\n" +
			"- \\\\server\\backups"
	)
	protected String backupFolder = "backups";
	
	@Option(section = BACKUP_SECTION,
		comment = 
			"Type of compression to use when storing backups.\n\n" +
			"Valid values:\n" +
			"- ZIP\n" +
			"- TAR\n" +
			"- TAR_GZ\n" +
			"- TAR_BZ2\n" +
			"- GIT\n" +
			"- NONE"
	)
	protected CompressionType compression = CompressionType.getDefault();
	
	@Option(section = BACKUP_SECTION, name = "configuration", comment = "Backup config folder.")
	protected boolean backupConfiguration = true;
	
	@Option(section = BACKUP_SECTION, name = "mods", comment = "Backup mods folder.")
	protected boolean backupMods = false;
	
	@Option(section = BACKUP_SECTION, name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean backupServerConfiguration = false;
	
	@Option(section = BACKUP_SECTION, name = "world", comment = "Backup world folder.")
	protected boolean backupWorld = true;
	
	@Option(section = BACKUP_SECTION,
		comment =
			"List of dimension id's to *not* backup. Use this to disable dimensions\n" +
			"that are large or unneeded. Currently it is impossible to disable\n" +
			"dimension 0 (the Overworld)\n\n" +
			"Example to disable the nether in backups:\n" +
			"I:disabledDimensions <\n" +
			"-1\n" +
			">"
	)
	protected int[] disabledDimensions = new int[] {};
	
	@Option(section = BACKUP_SECTION, name = "other", comment = "Other files or directories to backup.")
	protected String[] backupOthers = new String[] {};
	
	////////////////////////////////////////////////////////
	//                   LONGTERM                         //
	////////////////////////////////////////////////////////
	@Section(section = LONGTERM_BACKUP_SECTION, comment =
			"These settings control what and how things are backed up when doing an\n" +
			"archival backup. The file group settings are cumulative with the\n" +
			"regular backups. If you select to backup your world in the regular\n" +
			"backup, it will be enabled for longterm backups no matter what.\n" +
			"Disabled dimensions however do totally override the default settings."
	)
	protected ConfigCategory longtermBackup;
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "enabled", comment = "Whether to enable separate long-term backups.")
	protected boolean longtermEnabled = false;
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "backupFolder",
		comment =
			"Folder name to store backups in. Each world's backups will be stored in\n" +
			"subfolders of this one. This can be an absolute path.\n\n" +
			"Examples:\n" +
			"- backups\n" +
			"- C:\\backups\n" +
			"- \\\\server\\backups"
	)
	protected String longtermBackupFolder = "archives";
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "compression",
		comment = 
			"Type of compression to use when storing backups.\n\n" +
			"Valid values:\n" +
			"- zip\n" +
			"- tar\n" +
			"- tgz\n" +
			"- tbz2\n" +
			"- git\n" +
			"- none"
	)
	protected CompressionType longtermCompression = CompressionType.getDefault();
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "configuration", comment = "Backup config folder.")
	protected boolean longtermBackupConfiguration = true;
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "mods", comment = "Backup mods folder.")
	protected boolean longtermBackupMods = false;
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "serverConfiguration", comment = "Backup server configuration files. eg. server.properties, whitelist.txt")
	protected boolean longtermBackupServerConfiguration = false;
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "world", comment = "Backup world folder.")
	protected boolean longtermBackupWorld = true;
	
	@Option(section = LONGTERM_BACKUP_SECTION, name = "disabledDimensions",
		comment =
			"List of dimension id's to *not* backup. Use this to disable dimensions\n" +
			"that are large or unneeded. Currently it is impossible to disable\n" +
			"dimension 0 (the Overworld)\n\n" +
			"Example to disable the nether in backups:\n" +
			"I:disabledDimensions <\n" +
			"-1\n" +
			">"
	)
	protected int[] longtermDisabledDimensions = new int[] {};
	
	@Option(section = LONGTERM_BACKUP_SECTION, comment = "The number of daily archival backups to keep.")
	protected int maxDailyBackups = 7;
	
	@Option(section = LONGTERM_BACKUP_SECTION, comment = "The number of weekly archival backups to keep.")
	protected int maxWeeklyBackups = 2;
	
	////////////////////////////////////////////////////////
	//                   GETTERS                          //
	////////////////////////////////////////////////////////
	public int getBackupInterval() {
		return backupInterval;
	}
	
	public boolean onlyOperatorsCanManuallyBackup() {
		return opsOnly;
	}
	
	public boolean onlyRunBackupsWithPlayersOnline() {
		return backupOnlyWithPlayer;
	}
	
	public boolean canCommandBlocksUseCommands() {
		return commandBlocksAllowed;
	}
	
	public int getLoggingLevel() {
		return loggingLevel;
	}
	
	public boolean longtermBackupsEnabled() {
		return longtermEnabled;
	}
	
	public boolean shouldCheckForUpdates() {
		return checkUpdates;
	}
	
	public BackupSettings getRegularBackupSettings(MinecraftServer server) {
		return new BackupSettings(
				server, backupFolder, loggingLevel, new RegularBackupCleanup(maxBackups),
				backupWorld, backupConfiguration, backupMods, backupServerConfiguration, backupOthers,
				disabledDimensions, compression.getCompressionHandler(server));
	}

	public BackupSettings getFullBackupSettings(MinecraftServer server) {
		return new BackupSettings(server, backupFolder, loggingLevel, new RegularBackupCleanup(maxBackups),
				true, true, true, true, backupOthers,
				new int[] {}, compression.getCompressionHandler(server));
	}

	public BackupSettings getArchiveBackupSettings(MinecraftServer server) {
		return new BackupSettings(server, longtermBackupFolder, loggingLevel, new ArchiveBackupCleanup(maxDailyBackups, maxWeeklyBackups),
				backupWorld || longtermBackupWorld, backupConfiguration || longtermBackupConfiguration,
				backupMods || longtermBackupMods, backupServerConfiguration || longtermBackupServerConfiguration, backupOthers,
				longtermDisabledDimensions, longtermCompression.getCompressionHandler(server));
	}
	
	////////////////////////////////////////////////////////
	//                 /options section                   //
	////////////////////////////////////////////////////////
	
	public BackupConfiguration(File configFile) {
		super(ForgeBackup.LOGGER);
		init(configFile);
	}

	@Override
	protected void migrateOldOptions(Configuration config) {
		if (config.getCategory(Section.GENERAL).containsKey("backupFolder")) {
			String folder = config.getCategory(Section.GENERAL).get("backupFolder").getString();
			config.getCategory(BACKUP_SECTION).put("backupFolder", new Property("backupFolder", folder, Type.STRING));
			config.getCategory(Section.GENERAL).remove("backupFolder");
		}
		
		if (config.getCategory(Section.GENERAL).containsKey("verboseLogging")) {
			boolean verboseLogging = config.get(Section.GENERAL, "verboseLogging", false).getBoolean(false);
			config.getCategory(Section.GENERAL).put("loggingLevel", new Property("loggingLevel", verboseLogging ? "2" : "1", Type.INTEGER));
			config.getCategory(Section.GENERAL).remove("verboseLogging");
		}
	}
}
