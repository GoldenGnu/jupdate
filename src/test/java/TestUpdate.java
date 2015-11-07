
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
	public void EveAssetsProgramWriteProtected() {
		runWriteProtected("http://eve.nikr.net/test/update/program/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void EveAssetsProgramOpen() {
		runOpen("http://eve.nikr.net/test/update/program/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void EveAssetsDataWriteProtected() {
		runWriteProtected("http://eve.nikr.net/test/update/data/", JEVEASSETS_FILENAME);
	}

	@Test @Ignore
	public void EveAssetsDataOpen() {
		runOpen("http://eve.nikr.net/test/update/data/", JEVEASSETS_FILENAME);
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
		assertTrue(PROGRAM.update(link, getWriteProtected(filename)));
	}

	private void runOpen(String link, String filename) {
		System.out.println(getOpen(filename));
		assertTrue(PROGRAM.update(link, getOpen(filename)));
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

		@Override
		public boolean update(String link, String jarFile) {
			return super.update(link, jarFile);
		}
	}
}
