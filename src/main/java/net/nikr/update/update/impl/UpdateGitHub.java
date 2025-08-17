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
import java.util.List;
import net.nikr.update.SplashUpdater;
import net.nikr.update.io.ListGetter;
import net.nikr.update.io.LocalUtil;
import net.nikr.update.io.OnlineUtil;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;
import net.nikr.update.update.Updater;


public class UpdateGitHub implements Updater {

	private static final String FILENAME = "github.update";

	@Override
	public void update(String link, String jarFile) throws LocalError, OnlineError {
		//Download
		ListGetter getter = new ListGetter();
		List<String> gitHubData = getter.get(link + FILENAME);
		if (gitHubData.isEmpty()) {
			throw new OnlineError(FILENAME + " is empty");
		}
		String githubLink = gitHubData.remove(0);
		List<String> files = null;
		if (githubLink.endsWith(".zip")) {
			String filename = githubLink.substring(Math.max(githubLink.lastIndexOf("/") + 1, 0));
			File programZip = LocalUtil.getTempDir(filename);
			OnlineUtil.downloadFile(githubLink, programZip);
			//Unzip to temp
			files = LocalUtil.unzip(programZip, gitHubData);
		} else {
			throw new LocalError("File type not supported");
		}
		
		//Move file to program folder
		LocalUtil.move(jarFile, files);
		SplashUpdater.setProgress(100);
		//Start
		LocalUtil.execute(LocalUtil.isWindows() ? "javaw" : "java", "-jar", jarFile);
	}

	@Override
	public boolean use(String link, String jarFile) {
		return OnlineUtil.exists(link + FILENAME);
	}
	
}
