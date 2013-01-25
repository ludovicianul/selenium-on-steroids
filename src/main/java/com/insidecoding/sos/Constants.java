package com.insidecoding.sos;

/**
 * Holds constant values used within the project.
 * 
 * @author ludovicianul
 * 
 */
public final class Constants {

	/**
	 * Private constructor so that we don't allow instantiation.
	 */
	private Constants() {
		// we do not allow instantiation
	}

	/**
	 * Holds the default location for the <b>screenshotFolder</b> property.
	 */
	public static final String DEFAULT_SCREENSHOT_FOLDER = "logs/screenshots";

	/**
	 * Class holdings the browser names that need special configuration.
	 * 
	 * @author ludovicianul
	 * 
	 */
	public static class Browsers {
		/**
		 * Holds the string name for the Firefox browser.
		 */
		public static final String FIREFOX = "firefox";

		/**
		 * Holds the string name for the Chrome browser.
		 */
		public static final String CHROME = "chrome";
		/**
		 * Holds the string name for the Internet Explorer browser.
		 */
		public static final String IE = "ie";
	}

	/**
	 * Holds the possible values for the <b>runMode</b> property.
	 * 
	 * @author ludovicianul
	 * 
	 */
	public static class RunMode {
		/**
		 * This is the default value for the <b>runMode</b> property.
		 */
		public static final String NORMAL = "standalone";

		/**
		 * This is the string value that can be set to <b>runMode</b> so that
		 * the WebDriver starts in grid mode.
		 */
		public static final String GRID = "grid";
	}

	/**
	 * Holds values or the platforms.
	 * 
	 * @author ludovicianul
	 * 
	 */
	public static class Platform {
		/**
		 * Value for the Windows OS.
		 */
		public static final String WINDOWS = "WINDOWS";

		/**
		 * Value for the LINUX OS.
		 */
		public static final String LINUX = "LINUX";
	}

}
