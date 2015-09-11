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
import net.nikr.update.io.LocalUtil;
import net.nikr.update.io.OnlineUtil;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;
import net.nikr.update.update.Updater;


public class UpdateInstaller implements Updater {

	@Override
	public void update(String link, String jarFile) throws LocalError, OnlineError {
		File localInstallFile = LocalUtil.getUpdateDir("installer.jar");
		//Download installer
		OnlineUtil.downloadFile(link + "installer.jar", localInstallFile, localInstallFile, false);
		//Run installer
		LocalUtil.execute(
				LocalUtil.isWindows() ? "javaw" : "java"
				,"-DINSTALL_PATH=" + LocalUtil.getOutputDir(jarFile, false).getAbsolutePath().replace("\\", "/")
				,"-DrunOnExit=true"
				,"-jar"
				,LocalUtil.getUpdateDir("installer.jar").getAbsolutePath()
				,"-options-system"
				);
	}

	@Override
	public boolean use(String link, String jarFile) {
		return OnlineUtil.exists(link + "installer.jar") && OnlineUtil.exists(link + "installer.jar.md5");
	}	
}
