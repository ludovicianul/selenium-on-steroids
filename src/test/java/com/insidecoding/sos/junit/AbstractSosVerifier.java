package com.insidecoding.sos.junit;

import org.junit.Test;
import org.openqa.selenium.By;

public class AbstractSosVerifier extends AbstractSoSBase {

	@Test
	public void testCheckbox() throws Exception {
		driver.get("http://www.w3schools.com/html/tryit.asp?filename=tryhtml_checkbox");
		driver.switchTo().frame("view");
		helper.assertChecked(By.name("vehicle"));
	}

	@Override
	protected void doAdditionalSetUp() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doAdditionalTearDown() {
		// TODO Auto-generated method stub

	}
}
