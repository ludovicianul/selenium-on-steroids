package com.insidecoding.sos.junit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;

import com.insidecoding.sos.Constants;
import com.insidecoding.sos.io.FileUtils;
import com.insidecoding.sos.webdriver.WebDriverHelper;

/**
 * This is the base class for all your JUNIT/Selenium tests. By extendind this
 * class you'll have access to a preconfigured WebDriver instance based on your
 * parameters from {@code selenium.properties}. </br> You can set various
 * properties that allows you to configure the WebDriver instance
 * 
 * @author ludovicianul
 * 
 */
public abstract class AbstractSoSBase {

	protected WebDriver driver;
	protected WebDriverHelper helper;
	protected FileUtils fileUtil;
	private static final Logger LOG = Logger.getLogger(AbstractSoSBase.class);

	@Rule
	public TakeScreenshoptOnFailureRule screen = new TakeScreenshoptOnFailureRule();

	@Before
	public void setUp() {
		fileUtil = new FileUtils();
		fileUtil.loadPropertiesBundle("selenium");

		String browserName = fileUtil.getPropertyAsString("browser");
		String browserVersion = fileUtil.getPropertyAsString("browserVersion");
		String runMode = fileUtil.getPropertyAsString("runMode");
		String proxyHost = fileUtil.getPropertyAsString("proxyHost");
		String proxyPort = fileUtil.getPropertyAsString("proxyPort");
		String gridUrl = fileUtil.getPropertyAsString("gridUrl");
		String platform = fileUtil.getPropertyAsString("platform");
		String userAgent = fileUtil.getPropertyAsString("userAgent");
		String noProxyFor = fileUtil.getPropertyAsString("noProxyFor");
		String screenShotFolder = fileUtil
				.getPropertyAsString("screenshotFolder");

		boolean assumeAllCertsUntrusted = fileUtil
				.getPropertyAsBoolean("assumeAllCertsUntrusted");
		boolean jsEnabled = fileUtil.getPropertyAsBoolean("jsEnabled");
		boolean flakiness = fileUtil.getPropertyAsBoolean("flakiness");
		boolean acceptAllCerts = fileUtil
				.getPropertyAsBoolean("acceptAllCerts");
		String profileLocation = fileUtil.getPropertyAsString("browserProfile");
		boolean takeScreenshots = fileUtil
				.getPropertyAsBoolean("takeScreenshots");

		/**
		 * assign default values if these are null
		 */
		if (userAgent == null || userAgent.isEmpty()
				|| userAgent.startsWith("$")) {
			userAgent = null;
		}

		if (screenShotFolder == null || screenShotFolder.isEmpty()
				|| screenShotFolder.startsWith("$")) {
			screenShotFolder = Constants.DEFAULT_SCREENSHOT_FOLDER;
		}

		if (noProxyFor == null || noProxyFor.isEmpty()
				|| noProxyFor.startsWith("$")) {
			noProxyFor = null;
		}

		if (browserVersion == null || browserVersion.isEmpty()
				|| browserVersion.startsWith("$")) {
			browserVersion = null;
		}

		if (browserName == null || browserName.isEmpty()
				|| browserName.startsWith("$")) {
			browserName = Constants.Browsers.FIREFOX;
		}

		if (runMode == null || runMode.isEmpty() || runMode.startsWith("$")) {
			runMode = Constants.RunMode.NORMAL;
		}

		if (platform == null || platform.isEmpty() || platform.startsWith("$")) {
			platform = "LINUX";
		}

		if (proxyHost == null || proxyHost.isEmpty()
				|| proxyHost.startsWith("$")) {
			proxyHost = null;
		}

		if (proxyPort == null || proxyPort.isEmpty()
				|| proxyPort.startsWith("$")) {
			proxyPort = null;
		}

		WebDriverHelper.Builder driverBuilder = new WebDriverHelper.Builder();
		driver = driverBuilder.browser(browserName).runMode(runMode)
				.proxy(proxyHost, proxyPort, noProxyFor)
				.acceptAllCerts(acceptAllCerts).grid(gridUrl)
				.platform(platform).flackinessForIe(flakiness)
				.profileLocation(profileLocation).userAgent(userAgent)
				.jsEnabled(jsEnabled).browserVersion(browserVersion)
				.assumeAllCertsUtrusted(assumeAllCertsUntrusted).buildDriver();

		LOG.info("Driver returned: " + driver);

		helper = new WebDriverHelper(driver);
		screen.setDriver(driver);
		screen.setTakeScreenshot(takeScreenshots);

		this.doAdditionalSetUp();

	}

	@After
	public void tearDown() {
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

}
