package ch.scaille.tcwriter.pilot.selenium;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.remote.CapabilityType;

public abstract class WebDriverFactory<T extends WebDriverFactory<?, O>, O extends AbstractDriverOptions<?>> {

	public static final boolean IS_WINDOWS = System.getProperty("os.name").contains("indows");

	protected final LoggingPreferences logPrefs = new LoggingPreferences();

	protected final O options;

	public abstract T headless();

	public abstract T withUntrustedConnection();

	public abstract T withDriverLogs(String folder);

	public abstract T withSilentDownload(String folder);

	public abstract T withWebDriverPath(Path path);

	public abstract T withBinary(String binary);

	public abstract WebDriver build();

	protected WebDriverFactory(O options) {
		this.options = options;
		logPrefs.enable(LogType.BROWSER, Level.ALL);
	}

	protected Path webDriverPath() {
		return Paths.get(System.getProperty("user.home")).resolve("selenium/webdrivers");
	}

	public T withUntrustedConnections() {
		options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		return (T) this;
	}
	
	public static String withPlatformName(Path driver) {
		if (IS_WINDOWS) {
			return driver + "-windows-64bit.exe";
		}
		return driver + "-linux-64bit";
	}

	static String logFile(String folder, String basename) {
		return folder + File.separatorChar + basename + '-' + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
				+ ".log";
	}

	public static final class FirefoxDriverFactory extends WebDriverFactory<FirefoxDriverFactory, FirefoxOptions> {
		private final FirefoxProfile profile = new FirefoxProfile();

		public FirefoxDriverFactory() {
			super(new FirefoxOptions());
			withWebDriverPath(webDriverPath());
			options.addPreference("dom.disable_beforeunload", true);
			options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
			profile.setPreference("gfx.direct2d.disabled", true);
			profile.setPreference("layers.acceleration.disabled", true);
			profile.setPreference("toolkit.cosmeticAnimations.enabled", false);
			profile.setPreference("webgl.angle.try-d3d11", false); // fails on vmware
		}

		@Override
		public FirefoxDriverFactory withWebDriverPath(Path path) {
			System.setProperty("webdriver.gecko.driver", withPlatformName(path.resolve("geckodriver")));
			return this;
		}

		@Override
		public FirefoxDriverFactory withBinary(String binary) {
			options.setBinary(binary);
			return this;
		}

		@Override
		public FirefoxDriverFactory headless() {
			options.addArguments("-headless");
			return this;
		}

		@Override
		public FirefoxDriverFactory withDriverLogs(String logFolder) {
			options.setLogLevel(FirefoxDriverLogLevel.TRACE);
			System.setProperty(GeckoDriverService.GECKO_DRIVER_LOG_PROPERTY, logFile(logFolder, "firefox"));
			return this;
		}

		@Override
		public FirefoxDriverFactory withSilentDownload(String folder) {
			profile.setPreference("browser.download.dir", folder);
			profile.setPreference("browser.download.folderList", 2);
			profile.setPreference("browser.download.manager.showWhenStarting", false);
			profile.setPreference("browser.helperApps.alwaysAsk.force", false);
			profile.setPreference("browser.download.manager.showAlertOnComplete", false);
			profile.setPreference("browser.download.manager.closeWhenDone", true);
			profile.setPreference("dom.disable_open_during_load", true);
			return this;
		}

		@Override
		public FirefoxDriverFactory withUntrustedConnection() {
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(false);
			return withUntrustedConnections();
		}

		@Override
		public WebDriver build() {
			return new FirefoxDriver(options);
		}
	}

	public static FirefoxDriverFactory firefox() {
		return new FirefoxDriverFactory();
	}

	public static final class ChromeDriverFactory extends WebDriverFactory<ChromeDriverFactory, ChromeOptions> {

		private final Map<String, Object> prefs = new HashMap<>();

		private ChromeDriverFactory() {
			super(new ChromeOptions());
			withWebDriverPath(webDriverPath());
			options.setCapability(CapabilityType.UNHANDLED_PROMPT_BEHAVIOUR, "ignore");
			options.addArguments("--disable-notifications");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-webassembly-lazy-compilation");
			options.addArguments("--disable-lazy-frame-loading", "--disable-lazy-image-loading");
			options.addArguments("--disable-backgrounding-occluded-windows");
			options.addArguments("--disable-popup-blocking");
			if (IS_WINDOWS) {
				options.addArguments("--disable-gpu");
			}
			prefs.put("profile.default_content_settings.popups", 0);
			prefs.put("safebrowsing.enabled", "false");
			prefs.put("disable-popup-blocking", "true");
			options.setExperimentalOption("prefs", prefs);
		}

		@Override
		public ChromeDriverFactory withWebDriverPath(Path path) {
			System.setProperty("webdriver.chrome.driver", withPlatformName(path.resolve("geckodriver")));
			return this;
		}

		@Override
		public ChromeDriverFactory withBinary(String binary) {
			options.setBinary(binary);
			return this;
		}

		@Override
		public ChromeDriverFactory headless() {
			options.addArguments("--headless", "window-size=1920,1200", "--enable-features=NetworkService",
					"--disable-web-security");
			return this;
		}

		@Override
		public ChromeDriverFactory withDriverLogs(String logFolder) {
			System.setProperty("webdriver.chrome.verboseLogging", "true");
			System.setProperty("webdriver.chrome.logfile", logFile(logFolder, "chromedriver"));
			return this;
		}

		@Override
		public ChromeDriverFactory withSilentDownload(String folder) {
			prefs.put("download.directory_upgrade", "true");
			prefs.put("download.default_directory", folder);
			prefs.put("safebrowsing.disable_download_protection", "true");
			prefs.put("download.prompt_for_download", "false");
			return this;
		}

		@Override
		public ChromeDriverFactory withUntrustedConnection() {
			options.addArguments("--ignore-certificate-errors");
			return withUntrustedConnections();
		}

		@Override
		public WebDriver build() {
			return new ChromeDriver(options);
		}
	}

	public static ChromeDriverFactory chrome() {
		return new ChromeDriverFactory();
	}

}