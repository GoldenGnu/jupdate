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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.nikr.update.update.LocalError;

public class LocalUtil {

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
		List<String> commands = new ArrayList<String>(Arrays.asList(strings));
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
