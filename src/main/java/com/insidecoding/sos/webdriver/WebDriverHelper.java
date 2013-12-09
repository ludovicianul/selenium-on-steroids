package com.insidecoding.sos.webdriver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.security.Credentials;
import org.openqa.selenium.security.UserAndPassword;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.insidecoding.sos.Constants;

/**
 * This class decorates the WebDriver API with more helper methods.
 * 
 * @author ludovicianul
 * 
 */
public final class WebDriverHelper {

	/**
	 * The number of seconds to sleep.
	 */
	private static final int SECONDS_TO_SLEEP = 2000;

	/**
	 * The WebDriver instance being decorated.
	 */
	private WebDriver driver;

	/**
	 * Stores the main windows when dealing with multiple windows.
	 */
	private String mainWindow;

	/**
	 * The Logger for the class.
	 */
	private static final Logger LOG = Logger.getLogger(WebDriverHelper.class);

	/**
	 * You must pass a valid WebDriver instance.
	 * 
	 * @param d
	 *            the WebDriver instance
	 */
	public WebDriverHelper(final WebDriver d) {
		if (d == null) {
			throw new IllegalArgumentException("Driver is NULL!");
		}
		driver = d;
	}

	/**
	 * Highlights the specified element within the page *
	 * 
	 * 
	 * @param element
	 */
	public void highlightElement(WebElement element) {
		for (int i = 0; i < 2; i++) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "color: yellow; border: 2px solid yellow;");
			js.executeScript(
					"arguments[0].setAttribute('style', arguments[1]);",
					element, "");
		}
	}

	/**
	 * Refreshes the current page pressing CTRL + F5.
	 */
	public void clearCache() {
		throw new NotImplementedException();
	}

	/**
	 * Gets the current alert.
	 * 
	 * @return the current alert window
	 */
	public Alert getCurrentAlert() {
		return driver.switchTo().alert();
	}

	/**
	 * Chooses Cancel/No or any other "dismiss" button for the upcoming alert
	 * window. If no alert is displayed an exception will be thrown.
	 */
	public void chooseCancelOnNextAlert() {
		Alert alert = driver.switchTo().alert();

		alert.dismiss();
	}

	/**
	 * Chooses Yes/Ok or any other confirmation button for the upcoming alert
	 * window. If no alert is displayed an exception will be thrown.
	 */
	public void chooseConfirmOnNextAlert() {
		Alert alert = driver.switchTo().alert();

		alert.accept();
	}

	/**
	 * Enters the specified text into the upcoming input alert.
	 * 
	 * @param text
	 *            the text to be entered
	 */
	public void sendTextToNextAlert(String text) {
		Alert alert = driver.switchTo().alert();

		alert.sendKeys(text);
		alert.accept();
	}

	/**
	 * Authenticates the user using the given username and password. This will
	 * work only if the credentials are requested through an alert window.
	 * 
	 * @param username
	 *            the user name
	 * @param password
	 *            the password
	 */
	public void authenticateOnNextAlert(String username, String password) {
		Credentials credentials = new UserAndPassword(username, password);
		Alert alert = driver.switchTo().alert();

		alert.authenticateUsing(credentials);
	}

	/**
	 * Simulates pressing F5.
	 */
	public void refresh() {
		driver.navigate().refresh();
	}

	/**
	 * Takes a screenshot of the current screen.
	 * 
	 * @param filename
	 *            the name of the file where the screenshot will be saved
	 * @throws IOException
	 *             if something goes wrong while saving the screenshot
	 */
	public void takeScreenshot(String filename) throws IOException {
		File screenshot = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenshot, new File(filename));
	}

	/**
	 * Scrolls the page to the specified coordinates.
	 * 
	 * @param x
	 *            the X coordinate
	 * @param y
	 *            the Y coordinate
	 */
	public void scrollTo(final int x, final int y) {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(" + x
				+ "," + y + ");");
	}

	/**
	 * This method will scroll the page by 250 pixels every 2 seconds for as
	 * long as {@code numberOfSeconds}.
	 * 
	 * @param numberOfSeconds
	 *            the duration in seconds to scroll
	 */
	public void scrollContinuously(final int numberOfSeconds) {
		for (int i = 0; i < numberOfSeconds; i += 2) {
			((JavascriptExecutor) driver)
					.executeScript("window.scrollBy(0,250);");
			try {
				Thread.sleep(SECONDS_TO_SLEEP);
			} catch (InterruptedException e) {
				LOG.info("Error while scrolling!");
			}
		}
	}

	/**
	 * Call this method before using the driver into another window. Remember to
	 * call {@link WebDriverHelper.#dswitchBackToMainWindow()} when you want to
	 * return to the main window.
	 */
	public void storeMainWindow() {
		mainWindow = driver.getWindowHandle();
	}

	/**
	 * Switches back to the main window. You must call storeMainWindow() before
	 * calling this method.
	 */
	public void switchBackToMainWindow() {
		if (mainWindow == null) {
			throw new IllegalStateException(
					"storeMainWindow() was not called!!!");
		}
		driver.switchTo().window(mainWindow);
	}

	/**
	 * Checks if the given cookie name exists in the current session. This
	 * method is also logging the result. The match is CASE SENSITIVE.
	 * 
	 * @param cookieName
	 *            the name of the cookie
	 * @return true if the cookie exists or false otherwise
	 */
	public boolean verifyCookiePresentByName(final String cookieName) {
		Set<Cookie> cookies = driver.manage().getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				LOG.info("Cookie: " + cookieName + " was found with value: "
						+ cookie.getValue());
				return true;
			}
		}

		LOG.info("Cookie: " + cookieName + " NOT found!");
		return false;
	}

	/**
	 * Verifies that the given cookie name has the given cookie value. This
	 * method is also add logging info.
	 * 
	 * @param cookieName
	 *            the cookie name
	 * @param cookieValue
	 *            the cookie value
	 * @return true if the given cookie name exists and its value matches the
	 *         given cookie value; false otherwise
	 */
	public boolean verifyCookie(final String cookieName,
			final String cookieValue) {
		Set<Cookie> cookies = driver.manage().getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)
					&& cookie.getValue().equals(cookieValue)) {
				LOG.info("Cookie: " + cookieName + " was found with value: "
						+ cookie.getValue());
				return true;
			}
		}

		LOG.info("Cookie: " + cookieName + " with value: " + cookieValue
				+ " NOT found!");
		return false;
	}

	/**
	 * Verifies that the given element contains the given text.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param text
	 *            the text to be matched
	 * @return true if the given element contains the given text or false
	 *         otherwise
	 */
	public boolean verifyText(final By by, final String text) {
		WebElement element = driver.findElement(by);

		if (element.getText().equals(text)) {
			LOG.info("Element: " + element + " contains the given text: "
					+ text);
			return true;
		}

		LOG.info("Element: " + element + " does NOT contain the given text: "
				+ text);

		return false;
	}

	/**
	 * Verifies that the given checkbox is checked.
	 * 
	 * @param checkboxBy
	 *            the method of identifying the checkbox
	 * @return true if the given checkbox is checked or false otherwise
	 */
	public boolean verifyChecked(final By checkboxBy) {
		WebElement element = driver.findElement(checkboxBy);

		if (element.isSelected()) {
			LOG.info("Checkbox: " + element + " is checked!");
			return true;
		}

		LOG.info("Checkbox: " + element + " is NOT checked!");
		return false;
	}

	/**
	 * Checks if the given cookie name exists in the current session. This
	 * method is also logging the result. The match is CASE SENSITIVE. Please
	 * not that the method will do a JUNIT Assert causing the test to fail.
	 * 
	 * @param cookieName
	 *            the name of the cookie
	 * 
	 */
	public void assertCookiePresentByName(final String cookieName) {
		Set<Cookie> cookies = driver.manage().getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				LOG.info("Cookie: " + cookieName + " was found with value: "
						+ cookie.getValue());
				Assert.assertEquals(cookieName, cookie.getName());
				return;
			}
		}
		Assert.fail("The given cookie name: " + cookieName
				+ " is not present within the current session!");
	}

	/**
	 * Verifies that the given cookie name has the given cookie value. This
	 * method is also add logging info. Please not that the method will do a
	 * JUNIT Assert causing the test to fail.
	 * 
	 * @param cookieName
	 *            the cookie name
	 * @param cookieValue
	 *            the cookie value
	 */
	public void assertCookie(final String cookieName, final String cookieValue) {
		Set<Cookie> cookies = driver.manage().getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)
					&& cookie.getValue().equals(cookieValue)) {
				LOG.info("Cookie: " + cookieName + " was found with value: "
						+ cookie.getValue());
				return;
			}
		}

		Assert.fail("Cookie: " + cookieName + " with value: " + cookieValue
				+ " NOT found!");

	}

	/**
	 * Verifies that the given element contains the given text. Please not that
	 * the method will do a JUNIT Assert causing the test to fail.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param text
	 *            the text to be matched
	 */
	public void assertText(final By by, final String text) {
		WebElement element = driver.findElement(by);

		Assert.assertEquals("Element: " + element
				+ " does NOT contain the given text: " + text, text,
				element.getText());
	}

	/**
	 * Verifies that the given checkbox is checked. Please not that the method
	 * will do a JUNIT Assert causing the test to fail.
	 * 
	 * @param checkboxBy
	 *            the method of identifying the checkbox
	 * 
	 */
	public void assertChecked(final By checkboxBy) {
		WebElement element = driver.findElement(checkboxBy);

		Assert.assertTrue("Checkbox: " + element + " is NOT checked!",
				element.isSelected());
	}

	/**
	 * Checks if the specified element is present into the page.
	 * 
	 * @param by
	 *            method of identifying the element
	 * @return true if the element is present or false otherwise
	 */
	public boolean isElementPresent(final By by) {
		try {
			driver.findElement(by);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the supplied {@code element} contains another element
	 * identified by {@code by}.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param element
	 *            the root element that will be searched
	 * @return true if {@code element} contains an element identified by
	 *         {@code by} or false otherwise
	 */
	public boolean isElementPresent(final WebElement element, final By by) {
		try {
			element.findElement(by);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Waits until an element will be displayed into the page.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param maximumSeconds
	 *            maximum number of methods to wait for the element to be
	 *            present
	 */
	public void waitForElementPresent(final By by, final int maximumSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, maximumSeconds);
		wait.until(ExpectedConditions.presenceOfElementLocated((by)));
	}

	/**
	 * Waits until a text will be displayed within the page.
	 * 
	 * @param text
	 *            text to wait for
	 * @param maximumSeconds
	 *            maximum number of seconds to wait for the text to be present
	 */
	public void waitForTextPresentWithinPage(final String text,
			final int maximumSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, maximumSeconds);
		wait.until(ExpectedConditions.textToBePresentInElement(
				By.tagName("body"), text));
	}

	/**
	 * Waits until a WebElement will be displayed into the page.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param maximumSeconds
	 *            the maximum number of seconds to wait
	 */
	public void waitForElementToBeVisible(final By by, final int maximumSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, maximumSeconds);
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	/**
	 * Waits for an element to contain some text.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param maximumSeconds
	 *            the maximum number of seconds to wait
	 */
	public void waitForElementToContainText(final By by,
			final int maximumSeconds) {
		(new WebDriverWait(driver, maximumSeconds))
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(final WebDriver error1) {
						return !driver.findElement(by).getText().isEmpty();
					}
				});
	}

	/**
	 * Waits until an element contains a specific text.
	 * 
	 * @param by
	 *            method of identifying the element
	 * @param text
	 *            the element text to wait for
	 * @param maximumSeconds
	 *            the maximum number of seconds to wait for
	 */
	public void waitForElementToContainSpecificText(final By by,
			final String text, final int maximumSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, maximumSeconds);
		wait.until(ExpectedConditions.textToBePresentInElement(by, text));
	}

	/**
	 * Checks for presence of the text in a html page.
	 * 
	 * @param text
	 *            the text to be searched for
	 * @return true if the text is present within the page or false otherwise
	 */
	public boolean isTextPresentInPage(final String text) {
		WebElement body = driver.findElement(By.tagName("body"));
		return body.getText().contains(text);
	}

	/**
	 * Checks if a text is displayed in the drop-down. This method considers the
	 * actual display text. <br/>
	 * For example if we have the following situation: &lt;select id="test"&gt;
	 * &lt;option value="4"&gt;June&lt;/option&gt; &lt;/select&gt; we will call
	 * the method as follows: isValuePresentInDropDown(By.id("test"), "June");
	 * 
	 * @param by
	 *            the method of identifying the drop-down
	 * @param text
	 *            the text to search for
	 * @return true if the text is present or false otherwise
	 */
	public boolean isTextPresentInDropDown(final By by, final String text) {
		WebElement element = driver.findElement(by);
		List<WebElement> options = element.findElements(By
				.xpath(".//option[normalize-space(.) = " + escapeQuotes(text)
						+ "]"));
		return options != null && !options.isEmpty();
	}

	/**
	 * Checks if the VALUE is present in the drop-down. This considers the VALUE
	 * attribute of the option, not the actual display text. <br/>
	 * For example if we have the following situation: &lt;select id="test"&gt;
	 * &lt;option value="4"&gt;June&lt;/option&gt; &lt;/select&gt; we will call
	 * the method as follows: isValuePresentInDropDown(By.id("test"), "4");
	 * 
	 * @param by
	 *            he method of identifying the drop-down
	 * @param value
	 *            the value to search for
	 * @return true if the value is present in the drop-down or false otherwise
	 */
	public boolean isValuePresentInDropDown(final By by, final String value) {
		WebElement element = driver.findElement(by);

		StringBuilder builder = new StringBuilder(".//option[@value = ");
		builder.append(escapeQuotes(value));
		builder.append("]");
		List<WebElement> options = element.findElements(By.xpath(builder
				.toString()));

		return options != null && !options.isEmpty();
	}

	/**
	 * Select a value from a drop down list based on the actual value, NOT
	 * DISPLAYED TEXT.
	 * 
	 * @param by
	 *            the method of identifying the drop-down
	 * @param value
	 *            the value to select
	 */

	public void selectOptionFromDropdownByValue(final By by, final String value) {
		Select select = new Select(driver.findElement(by));
		select.selectByValue(value);
	}

	/**
	 * Select text from a drop down list based on the displayed text.
	 * 
	 * @param by
	 *            the method of identifying the drop-down
	 * @param displayText
	 *            the text based on which the value will be selected
	 */

	public void selectOptionFromDropdownByDisplayText(final By by,
			final String displayText) {
		Select select = new Select(driver.findElement(by));
		select.selectByVisibleText(displayText);
	}

	/**
	 * Checks if a text is selected in a drop down list. This will consider the
	 * display text not the actual value.
	 * 
	 * @param by
	 *            the method of identifying the drop-down
	 * @param displayText
	 *            the text to search for
	 * @return true if the text is selected or false otherwise
	 */

	public boolean isTextSelectedInDropDown(final By by,
			final String displayText) {
		WebElement element = driver.findElement(by);
		List<WebElement> options = element.findElements(By
				.xpath(".//option[normalize-space(.) = "
						+ escapeQuotes(displayText) + "]"));

		for (WebElement opt : options) {
			if (opt.isSelected()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the first selected value from a drop down list.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @return the first selected option from a drop-down
	 */
	public String getSelectedValue(final By by) {

		Select select = new Select(driver.findElement(by));
		String defaultSelectedValue = select.getFirstSelectedOption().getText();
		return defaultSelectedValue;

	}

	/**
	 * Checks if a value is selected in a drop down list.
	 * 
	 * @param by
	 *            the method of identifying the element
	 * @param value
	 *            the value to search for
	 * @return true if the value is selected or false otherwise
	 */

	public boolean isValueSelectedInDropDown(final By by, final String value) {
		WebElement element = driver.findElement(by);

		StringBuilder builder = new StringBuilder(".//option[@value = ");
		builder.append(escapeQuotes(value));
		builder.append("]");
		List<WebElement> options = element.findElements(By.xpath(builder
				.toString()));

		for (WebElement opt : options) {
			if (opt.isSelected()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Deselects all the elements from a drop down list.
	 * 
	 * @param by
	 *            the method of identifying the element
	 */

	public void deselectAllDropDownOptions(final By by) {
		new Select(driver.findElement(by)).deselectAll();
	}

	/**
	 * Select the supplied value from a radio button group.
	 * 
	 * @param radioButtonName
	 *            the name of the radio button
	 * @param value
	 *            the value to be selected
	 */

	public void selectRadioButtonByValue(final String radioButtonName,
			final String value) {
		List<WebElement> radioGroup = driver.findElements(By
				.name(radioButtonName));
		for (WebElement button : radioGroup) {
			if (button.getAttribute("value").equalsIgnoreCase(value)) {
				button.click();
				break;
			}
		}
	}

	/**
	 * Checks if a radio button group has the supplied value selected.
	 * 
	 * @param radioButtonName
	 *            the name of the radio button group
	 * @param value
	 *            the value for check for
	 * @return true if the value is selected or false otherwise
	 */

	public boolean isRadioButtonValueSelected(final String radioButtonName,
			final String value) {
		List<WebElement> radioGroup = driver.findElements(By
				.name(radioButtonName));
		for (WebElement button : radioGroup) {
			if (button.getAttribute("value").equalsIgnoreCase(value)
					&& button.isSelected()) {
				return true;

			}
		}

		return false;
	}

	/**
	 * Used to switch to a window by title.
	 * 
	 * @param title
	 *            the title of the window to switch to
	 */
	public void selectWindowByTitle(final String title) {
		String currentWindow = driver.getWindowHandle();
		Set<String> handles = driver.getWindowHandles();
		if (!handles.isEmpty()) {
			for (String windowId : handles) {
				if (!driver.switchTo().window(windowId).getTitle()
						.equals(title)) {
					driver.switchTo().window(currentWindow);
				}
			}
		}
	}

	/**
	 * Returns a WebDriver object that has the proxy server set.
	 * 
	 * @param proxyURL
	 *            the proxy host
	 * @param port
	 *            the proxy port
	 * @param driver
	 *            the drive class
	 * @return a new driver with the proxy settings configured
	 */
	public static WebDriver getDriverWithProxy(final String proxyURL,
			final String port, final WebDriver driver) {
		org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
		proxy.setHttpProxy(proxyURL + ":" + port)
				.setFtpProxy(proxyURL + ":" + port)
				.setSslProxy(proxyURL + ":" + port);

		DesiredCapabilities cap = new DesiredCapabilities();
		cap.setCapability(CapabilityType.PROXY, proxy);

		try {
			Constructor<?> c = driver.getClass().getConstructor(
					Capabilities.class);
			return (WebDriver) c.newInstance(cap);
		} catch (Exception e) {
			LOG.error(
					"Exception while creating a driver with the specified proxy",
					e);
		}
		return driver;
	}

	/**
	 * Creates a FirefoxDriver that has the supplied user agent.
	 * 
	 * @param userAgent
	 *            the user agent
	 * @return a new FirefoxDriver instance containing the specified user agent
	 *         settings
	 */

	public static WebDriver getFirefoxDriverWithUserAgent(final String userAgent) {
		ProfilesIni allProfiles = new ProfilesIni();
		FirefoxProfile profile = allProfiles.getProfile("default");
		profile.setPreference("general.useragent.override", userAgent);
		profile.setAcceptUntrustedCertificates(true);
		profile.setAssumeUntrustedCertificateIssuer(true);
		WebDriver driver = new FirefoxDriver(profile);
		return driver;

	}

	/**
	 * Returns a new WebDriver instance and loads an existing profile from the
	 * disk. You must pass the path to the profile.
	 * 
	 * @param pathToProfile
	 *            the path to the profile folder
	 * @return a new FirefoxDriver that loads the supplied profile
	 */
	public static WebDriver getFirefoxDriverWithExistingProfile(
			final String pathToProfile) {
		FirefoxProfile profile = new FirefoxProfile(new File(pathToProfile));

		return new FirefoxDriver(profile);
	}

	/**
	 * Escapes the quotes for the supplied string.
	 * 
	 * @param toEscape
	 *            string to be escaped
	 * @return an escaped string
	 */
	public String escapeQuotes(final String toEscape) {
		if (toEscape.indexOf("\"") > -1 && toEscape.indexOf("'") > -1) {
			boolean quoteIsLast = false;
			if (toEscape.indexOf("\"") == toEscape.length() - 1) {
				quoteIsLast = true;
			}
			String[] substrings = toEscape.split("\"");

			StringBuilder quoted = new StringBuilder("concat(");
			for (int i = 0; i < substrings.length; i++) {
				quoted.append("\"").append(substrings[i]).append("\"");
				quoted.append(((i == substrings.length - 1) ? (quoteIsLast ? ", '\"')"
						: ")")
						: ", '\"', "));
			}
			return quoted.toString();
		}

		if (toEscape.indexOf("\"") > -1) {
			return String.format("'%s'", toEscape);
		}

		return String.format("\"%s\"", toEscape);
	}

	/**
	 * Gets a profile with a specific user agent with or without javascript
	 * enabled.
	 * 
	 * @param userAgent
	 *            the user agent
	 * @param javascriptEnabled
	 *            javascript enabled or not
	 * @return a FirefoxDriver instance with javascript and user agent seetigs
	 *         configured
	 */
	public static WebDriver getFirefoxDriverWithJSSettings(
			final String userAgent, final boolean javascriptEnabled) {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("general.useragent.override", userAgent);
		profile.setPreference("javascript.enabled", javascriptEnabled);
		WebDriver driver = new FirefoxDriver(profile);
		return driver;

	}

	/**
	 * Returns a DesiredCapabilities object specific to the {@code browser}
	 * being sent as parameter. If the browser does not exists if default to
	 * firefox.
	 * 
	 * @param browser
	 *            the browser name
	 * @return a specific DesiredCapabilities object corresponding to the
	 *         supplied browser
	 */
	private static DesiredCapabilities getCapabilitiesBrowser(
			final String browser) {
		DesiredCapabilities capabilities = null;
		Method[] methods = DesiredCapabilities.class.getDeclaredMethods();
		for (Method m : methods) {
			if (m.getName().equalsIgnoreCase(browser)) {
				try {
					capabilities = (DesiredCapabilities) m.invoke(null,
							(Object[]) null);
				} catch (Exception e) {
					e.printStackTrace();
					LOG.debug("Browser: " + browser
							+ " is not valid. Defaulting to Firefox!");
				}
			}
		}

		if (capabilities == null) {
			LOG.debug("Browser: " + browser
					+ " is not valid. Defaulting to Firefox!");
			capabilities = DesiredCapabilities.firefox();
		}

		return capabilities;
	}

	/**
	 * Returns a web driver instance based on capabilities.
	 * 
	 * @param capabilities
	 *            the desired capabilities
	 * @param builder
	 *            the Builder instance
	 * @return a new WebDriver instance with the desired capabilities specified
	 */
	private static WebDriver getDriverInstance(
			final DesiredCapabilities capabilities, final Builder builder) {
		WebDriver driver = null;
		try {

			Class<?> clazz = Class.forName("org.openqa.selenium."
					+ builder.browserPackage + "."
					+ StringUtils.capitalize(builder.browser + "Driver"));
			LOG.info("Driver class to be returned: " + clazz);
			Constructor<?> c = clazz.getConstructor(Capabilities.class);

			LOG.info("Driver constructor to be called: " + c);
			driver = (WebDriver) c.newInstance(new Object[] { capabilities });
			LOG.info("Driver initialized: " + driver);
		} catch (Exception e) {
			throw new IllegalArgumentException("Browser " + builder.browser
					+ " is not a valid name!", e);

		}
		LOG.info("Returned driver instance: " + driver);
		return driver;
	}

	/**
	 * This method adds browser specific capabilities like FirefoxProfile and
	 * ChromeOptions.
	 * 
	 * @param capabilities
	 *            specific capabilities
	 * @param builder
	 *            the builder object containing the properties
	 */
	private static void addSpecificBrowserSettings(
			final DesiredCapabilities capabilities, final Builder builder) {
		if (capabilities.getBrowserName().equalsIgnoreCase(
				Constants.Browsers.FIREFOX)) {
			LOG.info("Browser is Firefox. Getting local profile");

			FirefoxProfile profile = null;

			if (builder.profileLocation != null
					&& !"".equalsIgnoreCase(builder.profileLocation)) {
				profile = new FirefoxProfile(new File(builder.profileLocation));
				LOG.info("Firefox profile: " + builder.profileLocation);
			} else {
				LOG.info("Loading Firefox default sprofile");
				ProfilesIni allProfiles = new ProfilesIni();
				allProfiles.getProfile("default");
			}

			capabilities.setCapability(FirefoxDriver.PROFILE, profile);

			if (builder.userAgent != null) {
				profile.setPreference("general.useragent.override",
						builder.userAgent);
			}

		} else if (capabilities.getBrowserName().equalsIgnoreCase(
				Constants.Browsers.CHROME)) {
			ChromeOptions options = new ChromeOptions();
			if (builder.userAgent != null) {
				options.addArguments("user-agent=" + builder.userAgent);
			}
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		} else if (capabilities.getBrowserName().equalsIgnoreCase(
				Constants.Browsers.IE)) {
			capabilities
					.setCapability(
							InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
							builder.flakinessForIe);
		}
		LOG.info("Finished adding specific browser settings");
	}

	/**
	 * This method returns a fully configured WebDriver instance based on the
	 * parameters sent.
	 * 
	 * @param builder
	 *            the Builder used to construct the WebDriver instance
	 * @return a fully configured FirefoxDriver instance
	 */
	private static WebDriver getDriver(final Builder builder) {
		// this is the default value
		DesiredCapabilities capabilities = getCapabilitiesBrowser(builder.browser);
		WebDriver driver = null;

		/**
		 * Setting the proxy
		 */
		LOG.info("Proxy seetings set");
		org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
		if (builder.proxyHost != null && !builder.proxyHost.isEmpty()) {
			if (!builder.proxyHost.equals("direct")) {
				proxy.setAutodetect(false);
				proxy.setHttpProxy(builder.proxyHost + ":" + builder.proxyPort)
						.setFtpProxy(
								builder.proxyHost + ":" + builder.proxyPort)
						.setSslProxy(
								builder.proxyHost + ":" + builder.proxyPort)
						.setNoProxy(builder.noProxyFor);
			} else {
				proxy.setProxyType(ProxyType.DIRECT);
			}
			capabilities.setCapability(CapabilityType.PROXY, proxy);
		}

		/**
		 * the Driver will take screenshots
		 */
		LOG.info("Screenshot capability set");
		capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);

		/**
		 * setting the platform: LINUX, MAC or Windows. Remember that sometimes
		 * the browsers are linked to a platform like IE for example
		 */
		if (builder.platform != null) {
			capabilities.setCapability(CapabilityType.PLATFORM,
					builder.platform);
		}

		if (builder.browserVersion != null) {
			capabilities.setCapability(CapabilityType.VERSION,
					builder.browserVersion);
		}

		/**
		 * set if javascript is enabled
		 */
		capabilities.setJavascriptEnabled(builder.jsEnabled);

		/**
		 * set if the browser will accept all certificates, including the
		 * self-signed ones
		 */
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS,
				builder.acceptAllCertificates);

		/**
		 * this will actually create firefox profiles, chrome options and others
		 */
		LOG.info("Adding specific browser settings");
		addSpecificBrowserSettings(capabilities, builder);

		/**
		 * getting an actual WebDriver implementation now
		 */
		LOG.info("Detecting running mode");
		if (builder.runMode.equalsIgnoreCase(Constants.RunMode.GRID)) {
			LOG.info("Run mode GRID. Setting GRID properties");
			try {
				driver = new RemoteWebDriver(new URL(builder.gridUrl),
						capabilities);
				((RemoteWebDriver) driver).setLogLevel(Level.SEVERE);
			} catch (Exception e) {
				LOG.debug("Exception while initiating remote driver:", e);
			}
		} else {
			LOG.info("Normal run mode. Getting driver instance");
			driver = getDriverInstance(capabilities, builder);
		}

		LOG.info("Returning the following driver: " + driver);
		LOG.info("With capabilities: " + capabilities);
		return driver;
	}

	/**
	 * Opens the specified URL using the specified cookie.
	 * 
	 * @param url
	 *            the url you want to open
	 * @param cookieName
	 *            the cookie name
	 * @param cookieValue
	 *            the cookie value
	 */
	public void goToUrlWithCookie(final String url, final String cookieName,
			final String cookieValue) {
		// we'll do a trick to prevent Selenium falsely reporting a cross
		// domain cookie attempt
		LOG.info("Getting: " + url + " with cookieName: " + cookieName
				+ " and cookieValue: " + cookieValue);
		driver.get(url + "/404.html"); // this should display 404 not found
		driver.manage().deleteAllCookies();
		driver.manage().addCookie(new Cookie(cookieName, cookieValue));
		driver.get(url);
	}

	/**
	 * Opens the specified URL using the specified cookies list.
	 * 
	 * @param url
	 *            the URL you want to open
	 * @param cookieNamesValues
	 *            the cookies list you want to pass
	 */
	public void goToUrlWithCookies(final String url,
			final Map<String, String> cookieNamesValues) {
		LOG.info("Getting: " + url + " with cookies: " + cookieNamesValues);
		driver.get(url + "/404.html"); // this should display 404 not found
		driver.manage().deleteAllCookies();

		Set<Entry<String, String>> entries = cookieNamesValues.entrySet();
		for (Entry<String, String> cookieEntry : entries) {
			driver.manage().addCookie(
					new Cookie(cookieEntry.getKey(), cookieEntry.getValue()));
		}
		driver.get(url);
	}

	/**
	 * Returns the contents of the table as a list. Each item in the list
	 * contains a table row.
	 * 
	 * @param tableBy
	 *            the method of identifying the table
	 * @return a list containing all the items within the table
	 */
	public List<List<String>> getTableAsList(final By tableBy) {
		List<List<String>> all = new ArrayList<List<String>>();
		WebElement table = driver.findElement(tableBy);

		List<WebElement> rows = table.findElements(By.tagName("tr"));
		for (WebElement row : rows) {
			List<String> toAdd = new ArrayList<String>();
			List<WebElement> columns = row.findElements(By.tagName("td"));
			for (WebElement column : columns) {
				toAdd.add(column.getText());
			}
			all.add(toAdd);
		}
		return all;
	}

	/**
	 * Returns the values for a specific column from a HTML table.
	 * 
	 * @param tableBy
	 *            the way to identify the table
	 * @param columnNumber
	 *            the column number
	 * @return a list with all the values corresponding to the specified column
	 */
	public List<String> getTableColumn(final By tableBy, final int columnNumber) {
		List<String> result = new ArrayList<String>();
		List<List<String>> table = this.getTableAsList(tableBy);

		for (List<String> line : table) {
			result.add(line.get(columnNumber));
		}
		return result;
	}

	/**
	 * Builder class used to create WebDriver instances.
	 * 
	 * @author ludovicianul
	 * 
	 */
	public static final class Builder {

		/**
		 * the user agent.
		 */
		private String userAgent;

		/**
		 * Javascript status.
		 */
		private boolean jsEnabled = true;
		/**
		 * The proxy host used if any.
		 */
		private String proxyHost;
		/**
		 * The proxy port used if any.
		 */
		private String proxyPort;

		/**
		 * Proxy bypass.
		 */
		private String noProxyFor;
		/**
		 * How to deal with certificates.
		 */
		private boolean acceptAllCertificates;
		/**
		 * 
		 */
		private boolean assumeAllCertsUntrusted;
		private String browser;
		private String browserVersion;
		private String platform;
		private String runMode = Constants.RunMode.NORMAL;
		private String gridUrl;
		private String browserPackage;
		private boolean flakinessForIe;
		private String profileLocation;

		public Builder flackinessForIe(final boolean flackiness) {
			this.flakinessForIe = flackiness;
			return this;
		}

		public Builder grid(final String gridURL) {
			this.gridUrl = gridURL;
			return this;
		}

		public Builder browser(final String b) {
			this.browser = b;
			if (b.startsWith("internet")) {
				browserPackage = "ie";
			} else {
				browserPackage = b.toLowerCase();
			}
			return this;
		}

		public Builder browserVersion(final String v) {
			this.browserVersion = v;
			return this;
		}

		public Builder platform(final String p) {
			this.platform = p;
			return this;
		}

		public Builder runMode(final String r) {
			this.runMode = r;
			return this;
		}

		public Builder proxy(final String prxyHost, final String prxyPort,
				final String noPrxyFor) {
			this.proxyHost = prxyHost;
			this.proxyPort = prxyPort;
			this.noProxyFor = noPrxyFor;
			return this;
		}

		public Builder userAgent(final String ua) {
			this.userAgent = ua;
			return this;
		}

		public Builder jsEnabled(final boolean jsOn) {
			this.jsEnabled = jsOn;
			return this;
		}

		public Builder acceptAllCerts(final boolean acceptAllCerts) {
			this.acceptAllCertificates = acceptAllCerts;
			return this;
		}

		public Builder assumeAllCertsUtrusted(final boolean assume) {
			this.assumeAllCertsUntrusted = assume;
			return this;
		}

		public Builder profileLocation(final String ffProfileLocation) {
			this.profileLocation = ffProfileLocation;
			return this;
		}

		@Override
		public String toString() {
			return "Builder [userAgent=" + userAgent + ", jsEnabled="
					+ jsEnabled + ", proxyHost=" + proxyHost + ", proxyPort="
					+ proxyPort + ", noProxyFor=" + noProxyFor
					+ ", acceptAllCertificates=" + acceptAllCertificates
					+ ", assumeAllCertsUntrusted=" + assumeAllCertsUntrusted
					+ ", browser=" + browser + ", browserVersion="
					+ browserVersion + ", platform=" + platform + ", runMode="
					+ runMode + ", gridUrl=" + gridUrl + ", browserPackage="
					+ browserPackage + "]" + ", profileLocation="
					+ profileLocation;
		}

		/**
		 * Creates a new WebDriver instance based on the properties supplied to
		 * the Builder.
		 * 
		 * @return a fully configured WebDriver instance
		 */
		public WebDriver buildDriver() {
			LOG.info("Returning a driver with the following settings: "
					+ this.toString());
			return getDriver(this);
		}
	}

}
