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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import net.nikr.update.update.LocalError;
import net.nikr.update.update.OnlineError;


public class OnlineUtil {
	
	public static boolean downloadFile(final String from, final File to, final File check, boolean sub) throws OnlineError, LocalError {
		DataGetter getter = new DataGetter();
		ListGetter listGetter = new ListGetter();
		HashGetter hashGetter = new HashGetter();
		List<String> list = listGetter.get(from + ".md5");
		if (list.isEmpty()) {
			throw new OnlineError("Failed to download md5 file:\r\n" + from + ".md5");
		} else {
			String checksum = list.get(0);
			boolean localAlreadyDone = hashGetter.get(check, checksum);
			pause();
			if (localAlreadyDone) {
				System.out.println(to.getName()+ " update not needed");
				return false;
			} else {
				getter.get(from, to, checksum, sub);
				pause();
				return true;
			}
		}
	}

	public static void downloadFile(final String from, final File to) throws OnlineError, LocalError {
		try {
			String location = from;
			URL url;
			do { //Handle redirects
				url = new URL(location);
				HttpURLConnection  connection = (HttpURLConnection) url.openConnection();
				connection.setInstanceFollowRedirects(false);
				location = connection.getHeaderField("Location");
			} while (location != null);
			try (InputStream in = url.openStream()) {
				Files.copy(in, to.toPath(), StandardCopyOption.REPLACE_EXISTING);
				System.out.println(from.substring(Math.max(from.lastIndexOf("/") + 1, 0)) + " downloaded");
			}
		} catch (IOException ex) {
			throw new OnlineError(ex);
		}
	}

	public static boolean exists(String URLName) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con = (HttpURLConnection) new URL(URLName).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		}
	}

	public static void pause() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static void breaksOn() {
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
