/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.update.update;

import net.nikr.update.update.impl.UpdateFileList;
import net.nikr.update.update.impl.UpdateGitHub;
import net.nikr.update.update.impl.UpdateInstaller;

/**
 *
 * @author Niklas
 */
public enum Updaters implements Updater {
	GITHUB_ZIP(new UpdateGitHub()),
	FILE_LIST(new UpdateFileList()),
	INSTALLER(new UpdateInstaller())
	;
	Updater updater;

	private Updaters(Updater updater) {
		this.updater = updater;
	}

	@Override
	public void update(String link, String jarFile) throws LocalError, OnlineError{
		updater.update(link, jarFile);
	}

	@Override
	public boolean use(String link, String jarFile) {
		return updater.use(link, jarFile);
	}
}
