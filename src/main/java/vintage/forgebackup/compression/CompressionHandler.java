package vintage.forgebackup.compression;

import net.minecraft.server.MinecraftServer;

import java.io.IOException;

public abstract class CompressionHandler implements ICompressionHandler {
    protected final String serverDataDirectory;

    public CompressionHandler(MinecraftServer server) {
        String directory;
        try {
            directory = server.getFile(".").getCanonicalPath();
        } catch (IOException ex) {
            directory = server.getFile(".").getAbsolutePath();
        }
        serverDataDirectory = directory;
    }

    protected String cleanPath(String path) {
        if (path.substring(0, serverDataDirectory.length()).equals(serverDataDirectory)) {
            return path.substring(serverDataDirectory.length() + 1);
        }

        return path;
    }
}
