package vintage.forgebackup.command;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import vintage.forgebackup.ForgeBackup;
import vintage.forgebackup.backup.Backup;

public class CommandBackup extends CommandBackupBase {

    public CommandBackup(FMLServerStartingEvent event) {
        super(event);
    }

    @Override
    public String getCommandName() {
        return "backup";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            if (!server.isDedicatedServer()) {
                return true;
            }
        }

        return super.canCommandSenderUseCommand(sender);
    }

    @Override
    public void processCommand(final ICommandSender sender, String[] args) {
        final Backup backup;

        if (args.length == 0) {
            server.getCommandManager().executeCommand(sender, "help " + this.getCommandName());
            return;
        }

        if ("reload".equals(args[0])) {
            ForgeBackup.instance().reloadConfiguration();
            sender.sendChatToPlayer("Configuration for forgebackup has been reloaded.");
            return;
        }

        if ("run".equals(args[0])) {
            backup = new Backup(ForgeBackup.instance().config().getRegularBackupSettings(server));
        } else if ("full".equals(args[0])) {
            backup = new Backup(ForgeBackup.instance().config().getFullBackupSettings(server));
        } else {
            backup = null;
            server.getCommandManager().executeCommand(sender, "help " + this.getCommandName());
        }

        if (backup != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    backup.run(sender);
                }
            }).start();
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + this.getCommandName() + " [run|full|reload]";
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
