package com.insidecoding.sos.xml;

import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestXMLUtils {
	private XMLUtils xmlUtils;

	@Before
	public void setUp() {
		xmlUtils = new XMLUtils();
	}

	@Test
	public void testSimpleTagValue() throws Exception {
		String tag = xmlUtils.getTagText(new File("files/myXML.xml"), "UTF-8",
				"name");

		Assert.assertEquals("John", tag);
	}
}
