package vintage.forgebackup.compression;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.minecraft.server.MinecraftServer;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

public class TarBzCompressionHandler extends TarCompressionHandler {
	public TarBzCompressionHandler(MinecraftServer server) {
		super(server);
	}

	@Override
	protected OutputStream getOutputStream(File backupFolder, String backupFilename) throws IOException {
		OutputStream bzipStream;
		try {
			OutputStream fileStream = new BufferedOutputStream(new FileOutputStream(new File(backupFolder, backupFilename)));
			bzipStream = new CompressorStreamFactory().createCompressorOutputStream(CompressorStreamFactory.BZIP2, fileStream);
		} catch (CompressorException e) {
			throw new IOException("Unable to create bzip stream.", e);
		}
		return bzipStream;
	}

	@Override
	public String getFileExtension() {
		return ".tar.bz2";
	}
}
