package vintage.forgebackup.compression;

import java.io.File;

import net.minecraft.server.MinecraftServer;

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
