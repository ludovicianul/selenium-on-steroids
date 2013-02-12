package com.insidecoding.sos.junit;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;

import com.insidecoding.sos.Constants;
import com.insidecoding.sos.io.FileUtils;
import com.insidecoding.sos.net.HttpCallUtils;
import com.insidecoding.sos.webdriver.WebDriverHelper;
import com.insidecoding.sos.xml.XMLUtils;

/**
 * This is the base class for all your JUNIT/Selenium tests. By extending this
 * class you'll have access to a preconfigured WebDriver instance based on your
 * parameters from {@code selenium.properties}. You can set various properties
 * that allows you to configure the WebDriver instance
 * 
 * @author ludovicianul
 * 
 */
public abstract class AbstractSoSBase {
	/**
	 * The WebDriver instance.
	 */
	protected WebDriver driver;

	/**
	 * The WebDriver helper instance.
	 */
	protected WebDriverHelper helper;

	/**
	 * Used to get the selenium properties.
	 */
	protected FileUtils fileUtil;

	/**
	 * Offers XML helper methods.
	 */
	protected XMLUtils xmlUtils;

	/**
	 * Offers HTTP call methods.
	 */
	protected HttpCallUtils httpUtils;

	/**
	 * The Logger for this class.
	 */
	private static final Logger LOG = Logger.getLogger(AbstractSoSBase.class);

	/**
	 * JUNIT Rule used to take screenshots on failure.
	 */
	@Rule
	public TakeScreenshotOnFailureRule screen = new TakeScreenshotOnFailureRule();

	/**
	 * Method called before starting each test.
	 */
	@Before
	public final void setUp() {
		fileUtil = new FileUtils();
		xmlUtils = new XMLUtils(fileUtil);
		httpUtils = new HttpCallUtils(fileUtil);

		fileUtil.loadPropertiesBundle("selenium");

		String browserName = fileUtil
				.getPropertyAsString("selenium", "browser");
		String browserVersion = fileUtil.getPropertyAsString("selenium",
				"browserVersion");
		String runMode = fileUtil.getPropertyAsString("selenium", "runMode");
		String proxyHost = fileUtil
				.getPropertyAsString("selenium", "proxyHost");
		String proxyPort = fileUtil
				.getPropertyAsString("selenium", "proxyPort");
		String gridUrl = fileUtil.getPropertyAsString("selenium", "gridUrl");
		String platform = fileUtil.getPropertyAsString("selenium", "platform");
		String userAgent = fileUtil
				.getPropertyAsString("selenium", "userAgent");
		String noProxyFor = fileUtil.getPropertyAsString("selenium",
				"noProxyFor");
		String screenShotFolder = fileUtil.getPropertyAsString("selenium",
				"screenshotFolder");

		boolean assumeAllCertsUntrusted = fileUtil.getPropertyAsBoolean(
				"selenium", "assumeAllCertsUntrusted");
		String jsEnabled = fileUtil
				.getPropertyAsString("selenium", "jsEnabled");
		boolean flakiness = fileUtil.getPropertyAsBoolean("selenium",
				"flakiness");
		boolean acceptAllCerts = fileUtil.getPropertyAsBoolean("selenium",
				"acceptAllCerts");
		String profileLocation = fileUtil.getPropertyAsString("selenium",
				"browserProfile");
		String takeScreenshots = fileUtil.getPropertyAsString("selenium",
				"takeScreenshots");

		boolean jsEnabledBoolean = Boolean.getBoolean(jsEnabled);
		boolean takeScreenshotsBoolean = Boolean.getBoolean(takeScreenshots);

		/**
		 * assign default values if these are null
		 */
		if (isPropertyNotSet(profileLocation)) {
			profileLocation = null;
		}
		if (isPropertyNotSet(userAgent)) {
			userAgent = null;
		}

		if (isPropertyNotSet(screenShotFolder)) {
			screenShotFolder = Constants.DEFAULT_SCREENSHOT_FOLDER;
		}

		if (isPropertyNotSet(noProxyFor)) {
			noProxyFor = null;
		}

		if (isPropertyNotSet(browserVersion)) {
			browserVersion = null;
		}

		if (isPropertyNotSet(browserName)) {
			browserName = Constants.Browsers.FIREFOX;
		}

		if (isPropertyNotSet(runMode)) {
			runMode = Constants.RunMode.NORMAL;
		}

		if (isPropertyNotSet(platform)) {
			platform = Constants.Platform.WINDOWS;
		}

		if (isPropertyNotSet(proxyHost)) {
			proxyHost = null;
		}

		if (isPropertyNotSet(proxyPort)) {
			proxyPort = null;
		}

		if (isPropertyNotSet(jsEnabled)) {
			jsEnabledBoolean = true;
		}

		if (isPropertyNotSet(takeScreenshots)) {
			takeScreenshotsBoolean = true;
		}

		screen.setTakeScreenshot(takeScreenshotsBoolean);
		screen.setScreenshotFolder(new File(screenShotFolder));

		WebDriverHelper.Builder driverBuilder = new WebDriverHelper.Builder();
		driver = driverBuilder.browser(browserName).runMode(runMode)
				.proxy(proxyHost, proxyPort, noProxyFor)
				.acceptAllCerts(acceptAllCerts).grid(gridUrl)
				.platform(platform).flackinessForIe(flakiness)
				.profileLocation(profileLocation).userAgent(userAgent)
				.jsEnabled(jsEnabledBoolean).browserVersion(browserVersion)
				.assumeAllCertsUtrusted(assumeAllCertsUntrusted).buildDriver();

		LOG.info("Driver returned: " + driver);

		helper = new WebDriverHelper(driver);
		screen.setDriver(driver);

		this.doAdditionalSetUp();
	}

	/**
	 * Method called after executing each test.
	 */
	@After
	public final void tearDown() {
		fileUtil.releaseResources();
		this.doAdditionalTearDown();
	}

	/**
	 * This method is called by the {@link #setpUp()} method at the beginning of
	 * each test in order to create the WebDriver instance that will be used in
	 * your tests mock data. Add any data that needs to be in place in order to
	 * run your tests
	 */
	protected abstract void doAdditionalSetUp();

	/**
	 * This method is called by the {@link #tearDown()} method at the end of
	 * each test in order to release resources (like for example quiting the
	 * WebDriver instance). You can add your own logic for releasing the
	 * resources created in the {@link #setUp()} method
	 */
	protected abstract void doAdditionalTearDown();

	/**
	 * Checks if the property is empty or is not set.
	 * 
	 * @param property
	 *            the name of the property
	 * @return true if the property is set or false is null, empty or starts
	 *         with $
	 */
	private boolean isPropertyNotSet(final String property) {
		return StringUtils.isEmpty(property) || property.startsWith("$");
	}

}
