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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.nikr.update.SplashUpdater;
import net.nikr.update.update.OnlineError;


public class DataGetter {

	public void get(String link, File out, String checksum, boolean sub) throws OnlineError {
		get(link, out, checksum, sub, 0);
	}

	private void get(String link, File out, String checksum, boolean sub, int tries) throws OnlineError {
		InputStream input = null;
		OutputStream output = null;
		int n;
		Exception exception = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			int max = con.getContentLength();
			double now = 0;
			if (sub) {
				SplashUpdater.setSubProgress(0);
			} else {
				SplashUpdater.setProgress(0);
			}
			
			byte[] buffer = new byte[4096];
			input = new DigestInputStream(con.getInputStream(), md);
			output = new FileOutputStream(out);
			while ((n = input.read(buffer)) != -1) {
				output.write(buffer, 0, n);
				now += 4096;
				if (now > 0 && max > 0) {
					if (sub) {
						SplashUpdater.setSubProgress((int)(now / max * 100.0));
					} else {
						SplashUpdater.setProgress((int)(now / max * 100.0));
					}
				}
			}
			output.flush();
			String sum = getToHex(md.digest());
			if (checksum.equals(sum)) {
				System.out.println(out.getName() + " downloaded");
				return; //OK
			} else {
				System.out.println(checksum + " is no match for " + sum);
			}
		} catch (MalformedInputException ex) {
			exception = ex;
		} catch (IOException ex) {
			exception = ex;
		} catch (NoSuchAlgorithmException ex) {
			exception = ex;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			if (output != null) {
				try {
					output.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		if (tries < 10){ //Retry 10 times
			out.delete();
			tries++;
			get(link, out, checksum, sub, tries);
		} else { //Failed 10 times, I give up...
			if (exception != null) {
				exception.printStackTrace();
				throw new OnlineError("Failed to download:\r\n" + link + "\r\nTo\r\n" + out.getName(), exception);
			} else {
				throw new OnlineError("Failed to download:\r\n" + link + "\r\nTo\r\n" + out.getName(), exception);
			}
		}
	}

	private String getToHex(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}
}
