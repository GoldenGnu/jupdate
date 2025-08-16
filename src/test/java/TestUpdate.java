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

import java.io.File;
import net.nikr.update.Program;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

public class TestUpdate {

	private static final String JEVEASSETS_FILENAME = "jEveAssets" + File.separator + "donotrun.jar";
	private static final String JWARFRAME_FILENAME = "jWarframe" + File.separator + "donotrun.jar";
	private static final TestProgram PROGRAM = new TestProgram();

	@Test @Ignore
	public void jEveAssetsTestProgramProtected() {
		runWriteProtected("http://eve.nikr.net/test/update/program/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsTestProgramOpen() {
		runOpen("http://eve.nikr.net/test/update/program/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsDataTestProtected() {
		runWriteProtected("http://eve.nikr.net/test/update/data/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsDataTestOpen() {
		runOpen("http://eve.nikr.net/test/update/data/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsLiveProgramProtected() {
		runWriteProtected("http://eve.nikr.net/jeveassets/update/program/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsLiveProgramOpen() {
		runOpen("http://eve.nikr.net/jeveassets/update/program/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsDataLiveProtected() {
		runWriteProtected("http://eve.nikr.net/jeveassets/update/data/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jEveAssetsDataLiveOpen() {
		runOpen("http://eve.nikr.net/jeveassets/update/data/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void jWarframeWriteProtected() {
		runWriteProtected("http://warframe.nikr.net/test/download/update/", JWARFRAME_FILENAME);
	}

	@Test @Ignore
	public void jWarframeOpen() {
		runOpen("http://warframe.nikr.net/test/download/update/", JWARFRAME_FILENAME);
	}

	private void runWriteProtected(String link, String filename) {
		System.out.println(getWriteProtected(filename));
		assertTrue(PROGRAM.updateStuff(link, getWriteProtected(filename)));
	}

	private void runOpen(String link, String filename) {
		System.out.println(getOpen(filename));
		assertTrue(PROGRAM.updateStuff(link, getOpen(filename)));
	}

	private String getWriteProtected(String filename) {
		File linux = new File("/usr/local/");
		if (linux.exists()) {
			return "/usr/local/" + filename;
		}
		File windows = new File("C:\\Program Files\\");
		if (windows.exists()) {
			return "C:\\Program Files\\" + filename;
		}
		return "";
	}

	private String getOpen(String filename) {
		return System.getProperty("user.home") + File.separator + filename;
	}

	private static class TestProgram extends Program {

		public TestProgram() {
			super();
		}

		public boolean updateStuff(String link, String jarFile) {
			return super.update(link, jarFile);
		}
	}
}
