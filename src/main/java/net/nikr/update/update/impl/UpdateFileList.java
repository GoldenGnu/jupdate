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

package net.nikr.update.update.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.nikr.update.SplashUpdater;
import net.nikr.update.io.ListGetter;
import net.nikr.update.io.LocalUtil;
import static net.nikr.update.io.LocalUtil.deleteDirectory;
import net.nikr.update.io.OnlineUtil;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;
import net.nikr.update.update.Updater;


public class UpdateFileList implements Updater {

	@Override
	public void update(String link, String jarFile) throws LocalError, OnlineError {
		//Download file list
		ListGetter getter = new ListGetter();
		List<String> files = getter.get(link + "list.php");
		if (files.isEmpty()) {
			throw new OnlineError("Failed to download file list:\r\n" + link + "list.php");
		}
		int increase = 100 / files.size();
		//Delete update directory
		File updateDir = LocalUtil.getUpdateDir();
		LocalUtil.deleteDirectory(updateDir);
		//Download files
		List<String> downloadedFiles = new ArrayList<String>();
		for (String filename : files) {
			SplashUpdater.addProgress(increase);
			boolean downloaded = OnlineUtil.downloadFile(link+filename, LocalUtil.getUpdateDir(filename), LocalUtil.getOutputDir(jarFile, filename, false), true);
			if (downloaded) {
				downloadedFiles.add(filename);
			}
		}
		//Move updated files to final destination
		for (String filename : downloadedFiles) {
			File from = LocalUtil.getUpdateDir(filename);
			File to = LocalUtil.getOutputDir(jarFile, filename, true);
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
				throw new LocalError("Failed to move file from:\r\n" + from.getAbsolutePath() + "\r\nto:\r\n" + to.getAbsolutePath());
			}
		}
		//Delete update directory
		deleteDirectory(updateDir);
		SplashUpdater.setProgress(100);
		LocalUtil.execute(LocalUtil.isWindows() ? "javaw" : "java", "-jar", jarFile);
	}

	/**
	 * Works without problems as long as we have write access
	 * @param link
	 * @param jarFile
	 * @return 
	 */
	@Override
	public boolean use(String link, String jarFile) {
		return OnlineUtil.exists(link + "list.php") && LocalUtil.canWrite(jarFile);
	}
}
