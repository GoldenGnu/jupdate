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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.junit.Test;


public class DataGetterTest {

	@Test
	public void testSomeMethod() {
		Assert.assertNotSame(0, getSize("http://eve.nikr.net/jeveassets/update/program/jeveassets.jar"));
	}

	public int getSize(String link) {
		try {
			URL url = new URL(link);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			return con.getContentLength();
		} catch (MalformedURLException ex) {
			
		} catch (IOException ex) {
			
		}
		return 0;
	}
	
}
