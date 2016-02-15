/**
 * 
 */
package access2csv;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for {@link Driver}.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class DriverTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	private File testDir;
	
	@Before
	public void setUp() throws Exception {
		testDir = tempDir.newFolder();
		// TODO: Copy test file into testDir. Note, it will be deleted automatically by TemporaryFolder after each test method
	}

	/**
	 * Test method for {@link access2csv.Driver#export(com.healthmarketscience.jackcess.Database, java.lang.String, java.io.Writer, boolean)}.
	 */
	@Ignore("TODO: Crerate sample file")
	@Test
	public final void testExportDatabaseStringWriterBoolean() throws Exception {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link access2csv.Driver#export(java.lang.String, java.lang.String)}.
	 */
	@Ignore("TODO: Crerate sample file")
	@Test
	public final void testExportStringString() throws Exception {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link access2csv.Driver#schema(java.lang.String)}.
	 */
	@Ignore("TODO: Crerate sample file")
	@Test
	public final void testSchema() throws Exception {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link access2csv.Driver#exportAll(java.lang.String, boolean)}.
	 */
	@Ignore("TODO: Crerate sample file")
	@Test
	public final void testExportAll() throws Exception {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link access2csv.Driver#main(java.lang.String[])}.
	 */
	@Test
	public final void testMainHelp() throws Exception {
		Driver.main(new String[] { "--help" });
		Driver.main(new String[] { "-h" });
	}

	/**
	 * Test method for {@link access2csv.Driver#main(java.lang.String[])}.
	 */
	@Test
	public final void testMainSchemaMissingDatabase() throws Exception {
		thrown.expect(FileNotFoundException.class);
		Driver.main(new String[] { "--input", "does-not-exist.accdb", "--schema" , "--output", new File(testDir, "dir-does-not-exist").toString()});
	}

	/**
	 * Test method for {@link access2csv.Driver#main(java.lang.String[])}.
	 */
	@Test
	public final void testHeaderMissingDatabase() throws Exception {
		thrown.expect(FileNotFoundException.class);
		Driver.main(new String[] { "--input", "does-not-exist.accdb", "--with-header" , "--output", new File(testDir, "dir-does-not-exist").toString()});
	}

}
