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
		File localInstallFile = LocalUtil.getTempDir("installer.jar");
		//Download installer
		OnlineUtil.downloadFile(link + "installer.jar", localInstallFile, localInstallFile, false);
		//Run installer
		if (LocalUtil.isWindows()) { //Windows
			LocalUtil.execute(
				"javaw"
				,"-DINSTALL_PATH=" + LocalUtil.getProgramDir(jarFile, false).getAbsolutePath().replace("\\", "/")
				,"-DrunOnExit=true"
				,"-jar"
				,LocalUtil.getTempDir("installer.jar").getAbsolutePath()
				,"-options-system"
				);
		} else if (LocalUtil.isUnix()) { //Unix
			//Update
			if (LocalUtil.executeTest("pkexec" ,"--help")) {
				//GUI 1/3 (ubuntu default)
				LocalUtil.execute(
					true
					,"pkexec"
					,"java"
					,"-DINSTALL_PATH=" + LocalUtil.getProgramDir(jarFile, false).getAbsolutePath().replace("\\", "/")
					//,"-DrunOnExit=true"
					,"-jar"
					,LocalUtil.getTempDir("installer.jar").getAbsolutePath()
					,"-options-system"
					);
			} else if (LocalUtil.executeTest("gksudo" ,"--help")) {
				//GUI 2/3
				LocalUtil.execute(
					true
					,"gksudo"
					,"--"
					,"java"
					,"-DINSTALL_PATH=" + LocalUtil.getProgramDir(jarFile, false).getAbsolutePath().replace("\\", "/")
					//,"-DrunOnExit=true"
					,"-jar"
					,LocalUtil.getTempDir("installer.jar").getAbsolutePath()
					,"-options-system"
					);
			} else if (LocalUtil.executeTest("kdesudo" ,"--help")) {
				//GUI 3/3
				LocalUtil.execute(
					true
					,"kdesudo"
					,"--"
					,"java"
					,"-DINSTALL_PATH=" + LocalUtil.getProgramDir(jarFile, false).getAbsolutePath().replace("\\", "/")
					//,"-DrunOnExit=true"
					,"-jar"
					,LocalUtil.getTempDir("installer.jar").getAbsolutePath()
					,"-options-system"
					);
			} else if (LocalUtil.executeTest("xterm" ,"--help")) {
				//CLI Fallback
				LocalUtil.execute(
					true
					,"xterm"
					,"-T"
					,"jUpdate installer"
					,"-e"
					,"sudo"
					,"java"
					,"-DINSTALL_PATH=" + LocalUtil.getProgramDir(jarFile, false).getAbsolutePath().replace("\\", "/")
					,"-jar"
					,LocalUtil.getTempDir("installer.jar").getAbsolutePath()
					,"-options-system"
					);
			} else {
				throw new LocalError("Failed to find a way to sudo\r\n\r\nInstall one of the following and retry:\r\n- pkexec\r\n- gksudo\r\n- kdesudo\r\n- xterm");
			}
			//Run jar
			LocalUtil.execute("java", "-jar", jarFile);
		} else { //Others (Mac untested)
			LocalUtil.execute(
				"java"
				,"-DINSTALL_PATH=" + LocalUtil.getProgramDir(jarFile, false).getAbsolutePath().replace("\\", "/")
				,"-DrunOnExit=true"
				,"-jar"
				,LocalUtil.getTempDir("installer.jar").getAbsolutePath()
				,"-options-system"
				);
		}
	}

	/**
	 * Works when we don't have write access
	 * @param link
	 * @param jarFile
	 * @return 
	 */
	@Override
	public boolean use(String link, String jarFile) {
		return OnlineUtil.exists(link + "installer.jar") && OnlineUtil.exists(link + "installer.jar.md5");
	}	
}
