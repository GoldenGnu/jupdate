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

import javax.swing.JOptionPane;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;
import net.nikr.update.update.Updaters;

public class Program {

	public static final String PROGRAM_VERSION = "2.0.0";

	public Program(final String link, final String jarFile) {
		this(link, jarFile, false);
	}

	public Program(final String link, final String jarFile, final boolean test) {
		update(link, jarFile);
		SplashUpdater.hide();
		if (!test) {
			System.exit(0);
		}
	}

	private void update(final String link, final String jarFile) {
		if (Updaters.INSTALLER.use(link, jarFile)) {
			try {
				Updaters.INSTALLER.update(link, jarFile);
			} catch (LocalError ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Local Error", JOptionPane.ERROR_MESSAGE);
			} catch (OnlineError ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Online Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (Updaters.FILE_LIST.use(link, jarFile)) {
			try {
				Updaters.FILE_LIST.update(link, jarFile);
			} catch (LocalError ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Local Error", JOptionPane.ERROR_MESSAGE);
			} catch (OnlineError ex) {
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Online Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "No download mirrors online\r\nPlease download the update from the official homepage", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
