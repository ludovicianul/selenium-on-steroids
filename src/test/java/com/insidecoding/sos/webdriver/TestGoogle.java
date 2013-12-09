package com.insidecoding.sos.webdriver;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.insidecoding.sos.junit.AbstractSoSBase;

public class TestGoogle extends AbstractSoSBase {

	@Test
	public void testFrontPage() throws Exception {
		driver.get("http://google.ro");

		WebElement search = driver.findElement(By.name("q"));
		search.sendKeys("test");
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
