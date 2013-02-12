package com.insidecoding.sos.io;

import java.io.IOException;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.junit.Test;

public class TestFileUtils {

	@Test
	public void testLoadPropertiesFromDefautContructor() throws Exception {
		FileUtils utils = new FileUtils();
		Assert.assertNotNull(utils.getBundle("propFile"));
		Assert.assertNull(utils.getBundle("otherBunlde"));
	}

	@Test
	public void testThatBundlesLoadsProperties() throws Exception {
		FileUtils utils = new FileUtils("files");
		ResourceBundle bundle = utils.getBundle("middleEarth");
		Assert.assertNotNull(bundle);

		/*
		 * We test the Strings
		 */
		Assert.assertEquals(bundle.getString("name"), "Frodo");
		Assert.assertEquals(utils.getPropertyAsString("name"), "Frodo");
		Assert.assertEquals(utils.getPropertyAsString("middleEarth", "name"),
				"Frodo");

		/*
		 * We test the Booleans
		 */
		Assert.assertEquals(utils.getPropertyAsBoolean("isHobbit"),
				Boolean.TRUE);
		Assert.assertEquals(
				utils.getPropertyAsBoolean("middleEarth", "isHobbit"),
				Boolean.TRUE);

		/*
		 * We test the Integers
		 */
		Assert.assertEquals(utils.getPropertyAsInteger("age"), 24);
		Assert.assertEquals(utils.getPropertyAsInteger("middleEarth", "age"),
				24);

		/*
		 * We test the Doubles
		 */
		Assert.assertEquals(utils.getPropertyAsDouble("height"), 120.5d);
		Assert.assertEquals(utils.getPropertyAsDouble("middleEarth", "height"),
				120.5d);

	}

	@Test
	public void testThatBundlesLoadsPropertiesWrongValues() throws Exception {
		FileUtils utils = new FileUtils("files");
		ResourceBundle bundle = utils.getBundle("middleEarth");
		Assert.assertNotNull(bundle);

		/*
		 * We test the Booleans
		 */
		Assert.assertEquals(utils.getPropertyAsBoolean("isHobbitWrong"),
				Boolean.FALSE);
		Assert.assertEquals(
				utils.getPropertyAsBoolean("middleEarth", "isHobbitWrong"),
				Boolean.FALSE);

		/*
		 * We test the Integers
		 */
		Assert.assertEquals(utils.getPropertyAsInteger("ageWrong"), -1);
		Assert.assertEquals(
				utils.getPropertyAsInteger("middleEarth", "ageWrong"), -1);

		/*
		 * We test the Doubles
		 */
		Assert.assertEquals(utils.getPropertyAsDouble("heightWrong"), -1.0);
		Assert.assertEquals(
				utils.getPropertyAsDouble("middleEarth", "heightWrong"), -1.0);
	}

	@Test
	public void testLoadPropertiesInexistentKeys() throws Exception {
		FileUtils utils = new FileUtils("files");

		Assert.assertNull(utils.getPropertyAsString("inexistent"));
		Assert.assertFalse(utils.getPropertyAsBoolean("inexistent"));

		Assert.assertEquals(utils.getPropertyAsInteger("in"), -1);
	}

	@Test
	public void testLoadPropertiesFromCustomLocation() throws Exception {
		FileUtils utils = new FileUtils("files");
		Assert.assertNotNull(utils.getBundle("middleEarth"));

		/*
		 * this is not there so it shouldn't be in the bundles
		 */
		Assert.assertNull(utils.getBundle("evilFile"));
		/*
		 * this is there but it is not a properties file
		 */
		Assert.assertNull(utils.getBundle("ring"));
	}

	@Test
	public void testLoadPropertiesNoLocation() throws Exception {
		try {
			new FileUtils("sauron");
			Assert.fail("Shouldn't get here");
		} catch (IOException e) {
			Assert.assertTrue(e.getMessage().indexOf("Invalid path") != -1);
		}
	}
}
