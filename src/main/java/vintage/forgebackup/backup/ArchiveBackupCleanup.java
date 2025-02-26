package vintage.forgebackup.backup;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArchiveBackupCleanup implements IBackupCleanup {
    protected final int maxDailyBackups;
    protected final int maxWeeklyBackups;

    public ArchiveBackupCleanup(int maxDailyBackups, int maxWeeklyBackups) {
        this.maxDailyBackups = maxDailyBackups;
        this.maxWeeklyBackups = maxWeeklyBackups;
    }

    @Override
    public String getBackupFilename() {
        Date now = new Date();
        return String.format("%TY%Tm%Td", now, now, now);
    }

    @Override
    public boolean runBackupCleanup(File backupDirectory) {
        for (File backup : backupDirectory.listFiles()) {
            Date backupDate;
            try {
                backupDate = parseFilename(backup.getName());
            } catch (ParseException e) {
                continue;
            }
            if (!isValidDailyBackup(backupDate) && !isValidWeeklyBackup(backupDate)) {
                backup.delete();
            }
        }

        return true;
    }

    private boolean isValidDailyBackup(Date backupDate) {
        Date now = new Date();

        long age = now.getTime() - backupDate.getTime();

        return age < maxDailyBackups * 24 * 60 * 60 * 1000;
    }

    private boolean isValidWeeklyBackup(Date backupDate) {
        Date now = new Date();

        long age = now.getTime() - backupDate.getTime();

        return backupDate.getDay() == 0 && age < maxWeeklyBackups * 7 * 24 * 60 * 60 * 1000;
    }

    private Date parseFilename(String filename) throws ParseException {
        return new SimpleDateFormat("yyyyMMdd").parse(filename.substring(0, 8));
    }
}
