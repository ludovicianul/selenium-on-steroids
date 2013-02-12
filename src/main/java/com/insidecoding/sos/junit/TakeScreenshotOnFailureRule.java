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
 * screenshots on failure.
 * 
 * @author ludovicianul
 * 
 */
public final class TakeScreenshotOnFailureRule implements TestRule {

	/**
	 * flag for taking screenshots.
	 */
	private boolean takeScreenshot;

	/**
	 * the WebDriver instance.
	 */
	private WebDriver driver;

	/**
	 * the folder where the screenshots will be saved.
	 */
	private File directory;

	/**
	 * the logger.
	 */
	private static final Logger LOG = Logger
			.getLogger(TakeScreenshotOnFailureRule.class);

	/**
	 * Set the folder where we save screenshots.
	 * 
	 * @param folder
	 *            the folder
	 */
	public void setScreenshotFolder(final File folder) {
		this.directory = folder;
		boolean created = directory.mkdirs();
		LOG.info("Screenshot folder created succsessfully: " + created);
	}

	/**
	 * Enables the ability to take screenshots.
	 * 
	 * @param takeScrshot
	 *            true if you want to take screenshots and false otherwise
	 */
	public void setTakeScreenshot(final boolean takeScrshot) {
		this.takeScreenshot = takeScrshot;
	}

	/**
	 * Sets the WebDriver instance.
	 * 
	 * @param drv
	 *            the WebDriver instance
	 */
	public void setDriver(final WebDriver drv) {
		this.driver = drv;
	}

	/**
	 * You must supply the folder where the screenshots will be stored in.
	 * 
	 * @param directory
	 */
	public TakeScreenshotOnFailureRule() {

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
							if (TakeScreenshotOnFailureRule.this.takeScreenshot) {
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
	 * Gets the name of the image file with the screenshot.
	 * 
	 * @param method
	 *            the method name
	 * @return the file that will cotain the screenshot
	 */
	private File filenameFor(final Description method) {
		String className = method.getClassName();
		String methodName = method.getMethodName();

		return new File(directory, className + "_" + methodName + ".png");
	}

	/**
	 * Saves the actual screenshot without interrupting the running of the
	 * tests. It will log an error if unable to store take the screenshot.
	 * 
	 * @param file
	 *            the file that will store the screenshot
	 */
	private void silentlySaveScreenshotTo(final File file) {
		try {
			saveScreenshotTo(file);
			LOG.info("Screenshot saved: " + file.getAbsolutePath());
		} catch (Exception e) {
			LOG.warn("Error while taking screenshot " + file.getName() + ": "
					+ e);
		}
	}

	/**
	 * Saves the screenshot to the file.
	 * 
	 * @param file
	 *            the file where to save the screenshot
	 * @throws IOException
	 *             if something goes wrong while saving
	 */
	private void saveScreenshotTo(final File file) throws IOException {
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
