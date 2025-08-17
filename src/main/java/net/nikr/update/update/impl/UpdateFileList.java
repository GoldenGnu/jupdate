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

import java.util.ArrayList;
import java.util.List;
import net.nikr.update.SplashUpdater;
import net.nikr.update.io.ListGetter;
import net.nikr.update.io.LocalUtil;
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
		//Download files
		List<String> downloadedFiles = new ArrayList<>();
		int progress = 0;
		for (String filename : files) {
			boolean downloaded = OnlineUtil.downloadFile(link+filename, LocalUtil.getTempDir(filename), LocalUtil.getProgramDir(jarFile, filename, false), true);
			if (downloaded) {
				downloadedFiles.add(filename);
			}
			progress++;
			SplashUpdater.setProgress((int)(100.0 * progress / files.size()));
		}
		//Move updated files to final destination
		LocalUtil.move(jarFile, downloadedFiles);
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
		return true;
	}
}
