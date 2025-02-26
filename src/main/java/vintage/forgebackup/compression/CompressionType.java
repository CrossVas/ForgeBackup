package vintage.forgebackup.compression;

import net.minecraft.server.MinecraftServer;
import vintage.forgebackup.ForgeBackup;

import java.lang.reflect.Constructor;
import java.util.logging.Level;

public enum CompressionType {
    ZIP(ZipCompressionHandler.class),
    TAR(TarCompressionHandler.class),
    TAR_GZ(TarGzCompressionHandler.class),
    TAR_BZ2(TarBzCompressionHandler.class),
    GIT(GitCompressionHandler.class),
    NONE(FolderCompressionHandler.class);

    private final Class<? extends ICompressionHandler> handlerType;

    CompressionType(Class<? extends ICompressionHandler> handlerType) {
        this.handlerType = handlerType;
    }

    public ICompressionHandler getCompressionHandler(MinecraftServer server) {
        try {
            Constructor<? extends ICompressionHandler> constructor = handlerType.getConstructor(MinecraftServer.class);
            return constructor.newInstance(server);
        } catch (Throwable e) {
            ForgeBackup.LOGGER.log(Level.SEVERE, String.format("Failed to create a new compression handler of type: %s", handlerType.getCanonicalName()));
            return null;
        }
    }

    /**
     * Get the default compression type on a given operating system.
     * <p>
     * Basically, this boils down to a check for Windows. We use zip by default
     * on Windows, tgz on everything else.
     */
    public static CompressionType getDefault() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return ZIP;
        } else {
            return TAR_GZ;
        }
    }
}
