package ch.scaille.tcwriter.pilot.selenium;

import java.io.File;
import java.nio.file.FileSystem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;

public interface WebDriverFactory<T extends WebDriverFactory<T>> {

	final boolean IS_WINDOWS = System.getProperty("os.name").contains("indows");

	T withBinary(String binary);

	T headless();

	T withUntrustedConnection();

	T withLogging(String folder);

	T withSilentDownload(String folder);

	WebDriver build();

	public static final class FirefoxDriverFactory implements WebDriverFactory<FirefoxDriverFactory> {
		private final FirefoxOptions options = new FirefoxOptions();
		private final FirefoxProfile profile = new FirefoxProfile();

		public FirefoxDriverFactory(String driverPath) {
			System.setProperty("webdriver.gecko.driver", driverPath);
			options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
			options.addPreference("dom.disable_beforeunload", true);
			options.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);

			profile.setPreference("gfx.direct2d.disabled", true);
			profile.setPreference("layers.acceleration.disabled", true);
			profile.setPreference("toolkit.cosmeticAnimations.enabled", false);
			profile.setPreference("webgl.angle.try-d3d11", false); // fails on vmware
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
		public FirefoxDriverFactory withLogging(String logFolder) {
			final String date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
			profile.setPreference("webdriver.log.browser.file",
					logFolder + File.pathSeparator + "firefox-browser-" + date + ".log");
			profile.setPreference("webdriver.log.driver.file",
					logFolder + File.pathSeparator + "firefox-driver-" + date + ".log");
			profile.setPreference("webdriver.log.file", logFolder + File.pathSeparator + "wd-log-" + date + ".log");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
					logFolder + File.pathSeparator + "firefox-log-" + date + ".log");
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
			options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.ACCEPT);
			options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(false);
			return this;
		}

		@Override
		public WebDriver build() {
			return new FirefoxDriver(options);
		}
	}

	public static FirefoxDriverFactory firefox(String driverPath) {
		return new FirefoxDriverFactory(driverPath);
	}

	public static final class ChromeDriverFactory implements WebDriverFactory<ChromeDriverFactory> {

		private final ChromeOptions options = new ChromeOptions();
		private final Map<String, Object> prefs = new HashMap<>();

		private ChromeDriverFactory(String driverPath) {
			System.setProperty("webdriver.chrome.driver", driverPath);
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
		public ChromeDriverFactory withLogging(String logFolder) {
			final String date = new SimpleDateFormat("YYYY-MM-dd").format(new Date());
			System.setProperty("webdriver.chrome.verboseLogging", "true");
			System.setProperty("webdriver.chrome.logfile",
					logFolder + File.pathSeparator + "chromedriver-" + date + ".log");
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
			options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			return this;
		}

		@Override
		public WebDriver build() {
			return new ChromeDriver();
		}
	}

	public static ChromeDriverFactory chrome(String driverPath) {
		return new ChromeDriverFactory(driverPath);
	}

}
