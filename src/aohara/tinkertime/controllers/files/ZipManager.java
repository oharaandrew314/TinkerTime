package aohara.tinkertime.controllers.files;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class ZipManager {
	
	private final Path zipPath;
	
	public ZipManager(Path zipPath){
		this.zipPath = zipPath;
	}

	public Set<ZipEntry> getZipEntries() {
		synchronized(zipPath){
			Set<ZipEntry> set = new HashSet<>();
			
			try (ZipFile zipFile = new ZipFile(zipPath.toFile())){
				set.addAll(Collections.list(zipFile.entries()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return set;
		}
	}

	public void unzipModule(Set<ZipEntry> entries, Path gameDataPath)
			throws IOException {
		synchronized(zipPath){
			try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
				for (ZipEntry entry : entries) {
					FileUtils.copyInputStreamToFile(
						zipFile.getInputStream(entry),
						gameDataPath.resolve(entry.getName()).toFile());
				}
			}
		}
	}

	public String getFileText(String fileName) {
		synchronized(zipPath){
			try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
				ZipEntry entry = zipFile.getEntry(fileName);
	
				StringWriter writer = new StringWriter();
				IOUtils.copy(zipFile.getInputStream(entry), writer);
				return writer.toString();
			} catch (IOException | NullPointerException e) {
				return null;
			}
		}
	}
}
