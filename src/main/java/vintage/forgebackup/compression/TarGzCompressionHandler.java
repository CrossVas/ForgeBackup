package vintage.forgebackup.compression;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;

public class TarGzCompressionHandler extends TarCompressionHandler {
    public TarGzCompressionHandler(MinecraftServer server) {
        super(server);
    }

    @Override
    protected OutputStream getOutputStream(File backupFolder, String backupFilename) throws IOException {
        OutputStream gzipStream;
        try {
            OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(new File(backupFolder, backupFilename)));
            gzipStream = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.GZIP, fileStream);
        } catch (CompressorException e) {
            throw new IOException("Unable to create gzip stream.", e);
        }
        return gzipStream;
    }

    @Override
    public String getFileExtension() {
        return ".tar.gz";
    }
}
