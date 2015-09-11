/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.nikr.update.io;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Niklas
 */
public class DataGetterTest {
	
	public DataGetterTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

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
