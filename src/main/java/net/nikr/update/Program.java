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
package net.nikr.update;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;
import net.nikr.update.update.Updaters;

public class Program {

	public static final String PROGRAM_VERSION = "2.1.1";
	private List<LocalError> localErrors = new ArrayList<LocalError>();
	private List<OnlineError> onlineErrors = new ArrayList<OnlineError>();

	protected Program() {}

	public Program(final String link, final String jarFile) {
		update(link, jarFile);
		SplashUpdater.hide();
		System.exit(0);
	}

	protected boolean update(String link, String jarFile) {
		boolean updated = false;
		if (Updaters.FILE_LIST.use(link, jarFile)) {
			try {
				Updaters.FILE_LIST.update(link, jarFile);
				updated = true; //Update okay
			} catch (LocalError ex) {
				localErrors.add(ex);
			} catch (OnlineError ex) {
				onlineErrors.add(ex);
			}
		}
		if (!updated && Updaters.INSTALLER.use(link, jarFile)) {
			try {
				Updaters.INSTALLER.update(link, jarFile);
				updated = true; //Update okay
			} catch (LocalError ex) {
				localErrors.add(ex);
			} catch (OnlineError ex) {
				onlineErrors.add(ex);
			}
		}
		if (!updated && localErrors.isEmpty() && onlineErrors.isEmpty()) {
			//Only show mirror error when no other errors happened
			JOptionPane.showMessageDialog(null, "No download mirrors online\r\nPlease download the update from the official homepage", "jUpdate: Error", JOptionPane.ERROR_MESSAGE);
		}
		if (!updated) { //Only show errors when everything failed
			for (LocalError localError : localErrors) {
				JOptionPane.showMessageDialog(null, localError.getMessage(), "jUpdate: Local Error", JOptionPane.ERROR_MESSAGE);
			}
			for (OnlineError onlineError : onlineErrors) {
				JOptionPane.showMessageDialog(null, onlineError.getMessage(), "jUpdate: Online Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		return updated;
	}
}
