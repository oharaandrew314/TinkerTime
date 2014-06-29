package aohara.tinkertime.controllers.files;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import aohara.tinkertime.models.ModStructure.Module;

public class ZipManager {

	public static Set<ZipEntry> getZipEntries(Path zipPath) {
		Set<ZipEntry> set = new HashSet<>();

		try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				set.add(entries.nextElement());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;
	}

	public static void unzipModule(Module module, Path gameDataPath)
			throws IOException {
		try (ZipFile zipFile = new ZipFile(module.getZipPath().toFile())) {
			for (ZipEntry entry : module.getEntries()) {
				FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry),
						gameDataPath.resolve(entry.getName()).toFile());
			}
		}
	}

	public static String getFileText(Path zipPath, String fileName) {
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
