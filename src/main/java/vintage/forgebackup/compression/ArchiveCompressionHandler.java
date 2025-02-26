package vintage.forgebackup.compression;

import net.minecraft.server.MinecraftServer;

import java.io.File;

public abstract class ArchiveCompressionHandler extends CompressionHandler {
    public ArchiveCompressionHandler(MinecraftServer server) {
        super(server);
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
