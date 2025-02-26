package vintage.forgebackup.backup;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.util.StatCollector;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import vintage.forgebackup.ForgeBackup;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class Backup {
    private final BackupSettings settings;

    public Backup(BackupSettings settings) {
        this.settings = settings;
    }

    public void run(ICommandSender sender) {

        while (settings.getServer().getTickCounter() % 900 >= 880 || settings.getServer().getTickCounter() % 900 < 10) {
            //noinspection EmptyCatchBlock
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

        notifyAdmins(sender, "ForgeBackup.backup.start");

        notifyAdmins(sender, Level.FINE, "ForgeBackup.save.disabled");
        toggleSavability(false);

        try {
            notifyAdmins(sender, Level.FINE, "ForgeBackup.save.force");
            forceSaveAllWorlds();

            notifyAdmins(sender, Level.FINE, "ForgeBackup.backup.progress");
            doBackup(sender);
        } catch (MinecraftException e) {
            notifyAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
            ForgeBackup.LOGGER.log(Level.SEVERE, e.getMessage());
            return;
        } catch (IOException e) {
            notifyAdmins(sender, Level.SEVERE, "ForgeBackup.backup.aborted");
            ForgeBackup.LOGGER.log(Level.SEVERE, e.getMessage());
            return;
        } finally {
            notifyAdmins(sender, Level.FINE, "ForgeBackup.save.enabled");
            toggleSavability(true);
        }

        notifyAdmins(sender, "ForgeBackup.backup.complete");
    }

    private void toggleSavability(boolean canSave) {
        for (int i = 0; i < settings.getServer().worldServers.length; ++i) {
            if (settings.getServer().worldServers[i] != null) {
                WorldServer worldServer = settings.getServer().worldServers[i];
                worldServer.canNotSave = !canSave;
            }
        }
    }

    private void forceSaveAllWorlds()
            throws MinecraftException {
        if (settings.getServer().getConfigurationManager() != null) {
            settings.getServer().getConfigurationManager().saveAllPlayerData();
        }

        for (int i = 0; i < settings.getServer().worldServers.length; ++i) {
            if (settings.getServer().worldServers[i] != null) {
                WorldServer var5 = settings.getServer().worldServers[i];
                boolean var6 = var5.canNotSave;
                var5.canNotSave = false;
                var5.saveAllChunks(true, null);
                var5.canNotSave = var6;
            }
        }
    }

    private void doBackup(ICommandSender sender)
            throws IOException {
        File backupsFolder = settings.getBackupFolder();
        if (backupsFolder.exists() && !backupsFolder.isDirectory()) {
            notifyAdmins(sender, Level.WARNING, "ForgeBackup.backup.folderExists");
            return;
        } else if (!backupsFolder.exists()) {
            backupsFolder.mkdirs();
        }

        if (!settings.getCompressionHandler().isValidTargetDirectory(backupsFolder)) {
            notifyAdmins(sender, Level.WARNING, "ForgeBackup.backup.folderInvalid");
            return;
        }

        if (!settings.getCompressionHandler().isIncremental()) {
            settings.getBackupCleanupHandler().runBackupCleanup(backupsFolder);
        }

        List<File> thingsToSave = settings.getFilesToBackup();
        List<Integer> disabledDimensions = settings.getDisabledDimensions();
        List<String> dimensionDirectories = Lists.newArrayList();

        for (int dimension : disabledDimensions) {
            WorldProvider provider = WorldProvider.getProviderForDimension(dimension);
            provider.setDimension(dimension);
            dimensionDirectories.add(provider.getSaveFolder());
        }

        settings.getCompressionHandler().openFile(backupsFolder, settings.getBackupFileName());

        while (!thingsToSave.isEmpty()) {
            File current = thingsToSave.remove(0);
            if (!current.exists()) {
                continue;
            }

            if (current.isDirectory()) {
                if (dimensionDirectories.contains(current.getName())) {
                    continue;
                }

                settings.getCompressionHandler().addCompressedFile(current);

                Collections.addAll(thingsToSave, current.listFiles());
            } else {
                settings.getCompressionHandler().addCompressedFile(current);
            }
        }

        settings.getCompressionHandler().closeFile();
    }

    public void notifyAdmins(ICommandSender sender, String translationKey, Object... parameters) {
        notifyAdmins(sender, Level.INFO, translationKey, parameters);
    }

    public void notifyAdmins(ICommandSender sender, Level level, String translationKey, Object... parameters) {
        boolean sendPlayerNotifications = !(settings.getLoggingLevel() == 0);
        String message = StatCollector.translateToLocalFormatted(translationKey, parameters);

        if (sender instanceof TileEntityCommandBlock && !settings.getServer().worldServers[0].getGameRules().getGameRuleBooleanValue("commandBlockOutput")) {
            sendPlayerNotifications = false;
        }

        if (sendPlayerNotifications) {
            for (Object playerObj : settings.getServer().getConfigurationManager().playerEntityList) {
                EntityPlayerMP player = (EntityPlayerMP) playerObj;
                if ((settings.getServer().getConfigurationManager().areCommandsAllowed(player.username) || !settings.getServer().isDedicatedServer()) &&
                        (settings.getLoggingLevel() == 2 || level != Level.FINE)) {
                    player.sendChatToPlayer("\u00a77\u00a7o[" + sender.getCommandSenderName() + ": " + message + "]");
                }
            }
        }

        if (settings.getLoggingLevel() == 2 && level == Level.FINE) {
            ForgeBackup.LOGGER.log(Level.INFO, message);
        } else {
            ForgeBackup.LOGGER.log(level, message);
        }
    }
}
