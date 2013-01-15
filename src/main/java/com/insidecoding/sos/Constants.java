package com.insidecoding.sos;

/**
 * Holds constant values used within the project
 * 
 * @author ludovicianul
 * 
 */
public final class Constants {

	/**
	 * Holds the default location for the <b>screenshotFolder</b> property
	 */
	public static final String DEFAULT_SCREENSHOT_FOLDER = "logs/screenshots";

	/**
	 * Class holdings the browser names that need special configuration
	 * 
	 * @author ludovicianul
	 * 
	 */
	public static class Browsers {
		public static final String FIREFOX = "firefox";
		public static final String CHROME = "chrome";
		public static final String IE = "ie";
	}

	/**
	 * Holds the possible values for the <b>runMode</b> property
	 * 
	 * @author ludovicianul
	 * 
	 */
	public static class RunMode {
		/**
		 * This is the default value for the <b>runMode</b> property
		 */
		public static final String NORMAL = "standalone";
		public static final String GRID = "grid";
	}

}
