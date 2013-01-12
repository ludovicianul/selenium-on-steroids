package com.insidecoding.sos.junit;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.WebDriver;

import com.insidecoding.sos.Constants;
import com.insidecoding.sos.io.FileUtils;
import com.insidecoding.sos.webdriver.WebDriverHelper;

/**
 * This is the base class for all your JUNIT/Selenium tests
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
	public TakeScreenshoptOnFailureRule screen = new TakeScreenshoptOnFailureRule(
			new File("./logs/screenshots"
					+ new SimpleDateFormat("dd-MM-yyyy").format(new Date())
					+ "/"));

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
	 * Implement this method in you own test classes to set up additional
	 * settings
	 */
	protected abstract void doAdditionalSetUp();

	/**
	 * Implement this method in you own test classes to do additional release of
	 * resources
	 */
	protected abstract void doAdditionalTearDown();

}
