package vintage.forgebackup.backup;

import java.io.File;

public interface IBackupCleanup {
    String getBackupFilename();

    boolean runBackupCleanup(File backupDirectory);
}
