package com.insidecoding.sos.junit;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;

import com.google.common.io.Files;

/**
 * This is a test rule used by the JUNIT framework and WebDriver to take
 * screenshots on failure
 * 
 * @author ludovicianul
 * 
 */
public class TakeScreenshoptOnFailureRule implements TestRule {

	private boolean takeScreenshot;
	private WebDriver driver;
	private File directory;
	private static final Logger LOG = Logger
			.getLogger(TakeScreenshoptOnFailureRule.class);

	public void setScreenshotFolder(File folder) {
		this.directory = folder;
		directory.mkdirs();
	}

	public void setTakeScreenshot(boolean takeScreenshot) {
		this.takeScreenshot = takeScreenshot;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * You must supply the folder where the screenshots will be stored in
	 * 
	 * @param directory
	 */
	public TakeScreenshoptOnFailureRule() {

	}

	@Override
	public Statement apply(final Statement st, final Description desc) {
		if (desc.isTest()) {
			return new Statement() {
				@Override
				public void evaluate() throws Throwable {
					try {
						st.evaluate();
					} catch (Throwable throwable) {
						try {
							if (TakeScreenshoptOnFailureRule.this.takeScreenshot) {
								silentlySaveScreenshotTo(filenameFor(desc));
							}
						} catch (Exception e) {
							LOG.debug("Error while taking screenshot "
									+ e.getMessage());
						}
						throw throwable;
					} finally {
						try {
							driver.quit();
						} catch (Exception e) {
							LOG.warn("Browser might be already closed!");
						}
					}
				}
			};
		} else {
			return st;
		}
	}

	/**
	 * Gets the name of the image file with the screenshot
	 * 
	 * @param method
	 *            - the method name
	 * @return the file that will cotain the screenshot
	 */
	private File filenameFor(Description method) {
		String className = method.getClassName();
		String methodName = method.getMethodName();

		return new File(directory, className + "_" + methodName + ".png");
	}

	/**
	 * Saves the actual screenshot without interrupting the running of the
	 * tests. It will log an error if unable to store take the screenshot
	 * 
	 * @param file
	 *            - the file that will store the screenshot
	 */
	private void silentlySaveScreenshotTo(File file) {
		try {
			saveScreenshotTo(file);
			LOG.debug("Screenshot saved: " + file.getAbsolutePath());
		} catch (Exception e) {
			LOG.warn("Error while taking screenshot " + file.getName() + ": "
					+ e);
		}
	}

	/**
	 * 
	 * @param file
	 * @throws IOException
	 */
	private void saveScreenshotTo(File file) throws IOException {
		byte[] bytes = null;
		try {
			bytes = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.BYTES);
		} catch (ClassCastException e) {
			WebDriver augmentedDriver = new Augmenter().augment(driver);
			bytes = ((TakesScreenshot) augmentedDriver)
					.getScreenshotAs(OutputType.BYTES);
		}

		Files.write(bytes, file);
	}

}
