package vintage.forgebackup.compression;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import vintage.forgebackup.ForgeBackup;
import net.minecraft.server.MinecraftServer;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

public class GitCompressionHandler extends CompressionHandler {
	Git git;
	String tagName;
	
	public GitCompressionHandler(MinecraftServer server) {
		super(server);
	}
	
	@Override
	public void openFile(File backupFolder, String backupFilename)
		throws IOException
	{
		try {
			tagName = backupFilename;
			boolean newRepo = false;
			
			File gitDir = new File(backupFolder, ".git");
			if (!gitDir.exists()) {
				new InitCommand().setDirectory(backupFolder).call();
				newRepo = true;
			}
			
			git = new Git(
				new RepositoryBuilder()
					.setWorkTree(backupFolder)
					.setGitDir(gitDir)
					.readEnvironment()
					.build()
			);
			
			if (!newRepo && !git.status().call().isClean()) {
				throw new IOException("Git repository is not clean, unable to continue.");
			}
			
			List<File> files = Lists.newArrayList(backupFolder.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String file) {
					return !".git".equals(file);
				}
			}));
			List<File> directories = Lists.newArrayList();
			
			while (!files.isEmpty()) {
				File file = files.remove(0);
				
				if (file.isDirectory()) {
					directories.add(file);
					Collections.addAll(files, file.listFiles());
				} else {
					git.rm().addFilepattern(cleanPath(file.getCanonicalPath())).call();
					file.delete();
				}
			}
			
			for (int i = directories.size() - 1; i >= 0; i--) {
				directories.get(i).delete();
			}
		} catch (GitAPIException e) {
			throw new IOException("There was an error communicating with git.", e);
		}
	}
	
	@Override
	public void addCompressedFile(File file) throws IOException {
		String cleanPath = cleanPath(file.getCanonicalPath());
		File target = new File(git.getRepository().getWorkTree(), cleanPath);
		if (!file.isDirectory()) {
			Files.copy(file, target);
		} else if (!target.isDirectory()) {
			if (!target.mkdirs()) {
				ForgeBackup.LOGGER.warning(String.format("Unable to create folder %s. This backup will likely fail soon.", target.getAbsoluteFile()));
			}
		}
		try {
			git.add().addFilepattern(cleanPath).setUpdate(false).call();
		} catch (GitAPIException e) {
			throw new IOException("There was an error communicating with git.", e);
		}
	}
	
	@Override
	public void closeFile() throws IOException {
		try {
			RevCommit commit = git.commit().setAuthor("ForgeBackup", "forgebackup@example.com").setMessage(tagName).call();
			git.tag().setObjectId(commit).setName(tagName).call();
		} catch (GitAPIException e) {
			throw new IOException("There was an error communicating with git.", e);
		}
	}
	
	@Override
	public String getFileExtension() {
		return "";
	}
	
	@Override
	public boolean isValidTargetDirectory(File directory) {
		return directory.listFiles().length == 0 || new File(directory, ".git").isDirectory();
	}
	
	@Override
	public boolean isIncremental() {
		return true;
	}
}
