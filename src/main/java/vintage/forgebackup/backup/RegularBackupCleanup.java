package vintage.forgebackup.backup;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RegularBackupCleanup implements IBackupCleanup {
    protected final int maxBackups;

    public RegularBackupCleanup(int maxBackups) {
        this.maxBackups = maxBackups;
    }

    @Override
    public boolean runBackupCleanup(File backupDirectory) {
        if (maxBackups <= 0) {
            return true;
        }

        List<File> backups = Lists.newArrayList(backupDirectory.listFiles());
        Collections.sort(backups);

        while (backups.size() >= maxBackups) {
            File backup = backups.remove(0);
            if (!backup.isDirectory()) {
                backup.delete();
            } else {
                List<File> files = Lists.newArrayList(backup);
                List<File> directories = Lists.newArrayList();

                while (!files.isEmpty()) {
                    File file = files.remove(0);

                    if (file.isDirectory()) {
                        directories.add(file);
                        Collections.addAll(files, file.listFiles());
                    } else {
                        file.delete();
                    }
                }

                for (int i = directories.size() - 1; i >= 0; i--) {
                    directories.get(i).delete();
                }
            }
        }

        return true;
    }

    @Override
    public String getBackupFilename() {
        Date now = new Date();
        return String.format("%TY%Tm%Td-%TH%TM%TS", now, now, now, now, now, now);
    }
}
