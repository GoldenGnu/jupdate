/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jUpdate.
 *
 * jUpdate is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jUpdate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jUpdate; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.update.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.nikr.update.update.LocalError;

public class LocalUtil {

	public static List<String> unzip(File zipFile, List<String> skip) throws LocalError {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			List<String> files = new ArrayList<>();
			ZipEntry entry;
			byte[] buffer = new byte[1024];
			boolean first = true;
			String replace = "";
			while ((entry = zis.getNextEntry()) != null) {
				String name = normalizeFilePath(entry.getName());
				name = name.replace(replace, "");
				if (skip(name, skip)) {
					continue;
				}
				File newFile = new File(getTempDir() + File.separator + name);
				if (entry.isDirectory()) {
					if (first) {
						replace = name;
					} else {
						newFile.mkdirs();
					}
				} else {
					new File(newFile.getParent()).mkdirs();
					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int length;
						while ((length = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, length);
						}
					}
					files.add(name);
				}
				first = false;
			}
			return files;
		} catch (IOException ex) {
			throw new LocalError(ex);
		}
	}

	private static boolean skip(String name, List<String> skip) {
		for (String s : skip) {
			if (name.toLowerCase().startsWith(normalizeFilePath(s.toLowerCase()))) {
				return true;
			}
		}
		return false;
	}

	private static String normalizeFilePath(String s) {
		return s.replace("/", File.separator).replace("\\", File.separator);
	}

	public static void move(String jarFile, List<String> files) throws LocalError {
		for (String filename : files) {
			File from = getTempDir(filename);
			File to = getProgramDir(jarFile, filename, true);
			try {
				Files.move(from.toPath(), to.toPath(), REPLACE_EXISTING);
			} catch (NoSuchMethodError ex) {
				java6move(files, jarFile);
				break;
			} catch (IOException ex) {
				throw new LocalError("Failed to move file from:\r\n" + from.getAbsolutePath() + "\r\nto:\r\n" + to.getAbsolutePath());
			}
		}
	}

	private static void java6move(List<String> downloadedFiles, String jarFile) throws LocalError {
		for (String filename : downloadedFiles) {
			File from = LocalUtil.getTempDir(filename);
			File to = LocalUtil.getProgramDir(jarFile, filename, true);
			if (to.exists()) { //Delete old file
				boolean delete = to.delete();
				if (!delete) {
					throw new LocalError("Failed to delete file:\r\n" + to.getName());
				}
			}
			boolean renamed = from.renameTo(to);
			if (renamed) {
				System.out.println(to.getName() + " moved");
			} else {
				throw new LocalError("Failed to move file from:\r\n"
						+ from.getAbsolutePath() + "\r\n"
						+ "to:\r\n"
						+ to.getAbsolutePath() + "\r\n"
						+ "\r\n"
						+ "Updating to Java 7 or later may fix the problem"
						);
			}
		}
	}

	public static File getTempDir() throws LocalError {
		return LocalUtil.getTempDir("never-used-filename").getParentFile();
	}

	public static File getTempDir(final String filename) throws LocalError {
		File userDir = new File(System.getProperty("user.home", "."));
		File file = new File(userDir.getAbsolutePath() + File.separator + ".jupdate" + File.separator + filename);
		File parentDir = file.getParentFile();
		if (!parentDir.exists() && !parentDir.mkdirs()) {
			throw new LocalError("Failed make directories:\r\n" + parentDir.getAbsolutePath());
		}
		return file;
	}

	public static File getProgramDir(final String jarFile, final boolean mkdirs) throws LocalError {
		return LocalUtil.getProgramDir(jarFile, "never-used-filename", mkdirs).getParentFile();
	}

	public static File getProgramDir(final String jarFile, final String filename, final boolean mkdirs) throws LocalError {
		File file = new File(jarFile);
		file = new File(file.getParent() + File.separator + filename);
		File parentDir = file.getParentFile();
		if (mkdirs && !parentDir.exists() && !parentDir.mkdirs()) {
			throw new LocalError("Failed make directories:\r\n" + parentDir.getAbsolutePath());
		}
		return file;
	}

	public static void execute(final String... commands) throws LocalError {
		execute(false, commands);
	}

	public static void execute(final boolean wait, final String... strings) throws LocalError {
		ProcessBuilder processBuilder = new ProcessBuilder();
		List<String> commands = new ArrayList<>(Arrays.asList(strings));
		Collections.replaceAll(commands, "java", System.getProperty("java.home") + File.separator + "bin" + File.separator + "java");
		Collections.replaceAll(commands, "javaw", System.getProperty("java.home") + File.separator + "bin" + File.separator + "javaw");
		//processBuilder.redirectErrorStream(true);
		processBuilder.directory(getJavaHome());
		System.out.println("execute:" + Arrays.toString(commands.toArray()));
		processBuilder.command(commands);
		try {
			Process process = processBuilder.start();
			if (wait) {
				try {
					process.waitFor();
				} catch (InterruptedException ex) {
					//No problem
				}
			}
		} catch (IOException ex) {
			throw new LocalError("Failed run restart command", ex);
		}
	}

	public static boolean executeTest(final String... commands) throws LocalError {
		ProcessBuilder processBuilder = new ProcessBuilder();
		//processBuilder.redirectErrorStream(true);
		processBuilder.directory(getJavaHome());
		System.out.println("execute:" + Arrays.toString(commands));
		processBuilder.command(commands);
		try {
			Process process = processBuilder.start();
			return true;
		} catch (IOException ex) {
			
		}
		return false;
	}

	public static boolean canWrite(String jarFile) {
		try {
			//Get test file
			File outputDir = LocalUtil.getProgramDir(jarFile, "write_test", true);
			//Create the test file
			if (!outputDir.createNewFile()) {
				return false;
			}
			//Delete the test file
			if (!outputDir.delete()) {
				return false;
			}
			return true; //All good
		} catch (LocalError ex) {
			return false;
		} catch (IOException ex) {
			return false;
		}
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	public static boolean isUnix() {
		return (System.getProperty("os.name").contains("nix") || System.getProperty("os.name").contains("nux") || System.getProperty("os.name").indexOf("aix") > 0 );
		
	}

	private static File getJavaHome() {
		return new File(System.getProperty("java.home") + File.separator + "bin");
	}
}
