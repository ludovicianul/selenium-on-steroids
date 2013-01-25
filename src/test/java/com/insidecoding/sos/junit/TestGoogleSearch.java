package com.insidecoding.sos.junit;

import org.junit.Test;

import com.insidecoding.sos.junit.AbstractSoSBase;

/**
 * Test class for some simple functionalities.
 * 
 * @author ludovicianul
 * 
 */
public class TestGoogleSearch extends AbstractSoSBase {

	// private static final Logger LOG =
	// Logger.getLogger(TestGoogleSearch.class);

	@Test
	public void testSimpleSearch() throws Exception {
		System.out.println("Driver received: " + driver);
	}

	@Override
	protected void doAdditionalSetUp() {
		// no need

	}

	@Override
	protected void doAdditionalTearDown() {
		// no need

	}
}
