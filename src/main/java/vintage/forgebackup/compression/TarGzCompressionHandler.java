package vintage.forgebackup.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

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
