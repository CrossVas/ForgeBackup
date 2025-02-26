package vintage.forgebackup.compression;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import vintage.forgebackup.ForgeBackup;

import java.io.*;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.BIGNUMBER_POSIX;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_POSIX;

public class TarCompressionHandler extends ArchiveCompressionHandler {
    protected TarArchiveOutputStream tarStream;

    public TarCompressionHandler(MinecraftServer server) {
        super(server);
    }

    protected OutputStream getOutputStream(File backupFolder, String backupFilename) throws IOException {
        return new BufferedOutputStream(new FileOutputStream(new File(backupFolder, backupFilename)));
    }

    @Override
    public String getFileExtension() {
        return ".tar";
    }

    @Override
    public void openFile(File backupFolder, String backupFilename) throws IOException {
        try {
            OutputStream outputStream = getOutputStream(backupFolder, backupFilename);
            tarStream = (TarArchiveOutputStream) new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.TAR, outputStream);
            //handle cases if there are long file names (over 100 chars) or large files (8Gib+)
            tarStream.setLongFileMode(LONGFILE_POSIX);
            tarStream.setBigNumberMode(BIGNUMBER_POSIX);
        } catch (ArchiveException e) {
            throw new IOException("Unable to create tar stream.", e);
        }
    }

    @Override
    public void addCompressedFile(File file) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(file);
        tarStream.putArchiveEntry(entry);

        // Tar supports adding directory entries, but they shouldn't have any data!
        if (!file.isDirectory()) {
            try {
                byte[] buffer = new byte[16384];
                int readBytes;
                InputStream inputStream = new FileInputStream(file);
                while ((readBytes = inputStream.read(buffer)) >= 0) {
                    tarStream.write(buffer, 0, readBytes);
                }
                inputStream.close();
            } catch (IOException e) {
                ForgeBackup.LOGGER.warning(String.format("Couldn't backup file: %s", file.getPath()));
            }
        }

        tarStream.closeArchiveEntry();
    }

    @Override
    public void closeFile() throws IOException {
        tarStream.close();
    }
}
