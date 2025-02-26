package vintage.forgebackup.command;

import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.server.MinecraftServer;
import vintage.forgebackup.ForgeBackup;

public abstract class CommandBackupBase extends CommandBase {
    protected final MinecraftServer server;

    public CommandBackupBase(FMLServerStartingEvent event) {
        this.server = event.getServer();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return !ForgeBackup.instance().config().onlyOperatorsCanManuallyBackup() ? 0 :
                (ForgeBackup.instance().config().canCommandBlocksUseCommands() ? 3 : 2);
    }
}
