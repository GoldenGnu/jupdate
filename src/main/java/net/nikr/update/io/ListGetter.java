/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jUpdate.
 *
 * Original code from jWarframe (https://code.google.com/p/jwarframe/)
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ListGetter {

	public List<String> get(String link) {
		BufferedReader in = null;
		List<String> raw = new ArrayList<String>();
		try {
			URL url = new URL(link);

			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String str;
			while ((str = in.readLine()) != null) {
				raw.add(str);
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to get: " + link);
		} catch (IOException e) {
			throw new RuntimeException("Failed to get: " + link);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					//I give up...
				}
			}
		}
		return raw;
	}
}
