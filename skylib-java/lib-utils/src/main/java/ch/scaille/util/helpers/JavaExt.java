package ch.scaille.util.helpers;

import java.net.URI;
import java.net.URL;
import java.util.TimerTask;

public class JavaExt {

	private JavaExt() {
		// noop
	}

	public static TimerTask timerTask(Runnable runnable) {
		return new TimerTask() {

			@Override
			public void run() {
				runnable.run();
			}
		};
	}

	public static String pathOf(URI uri) {
		return pathOf(uri.getPath());
	}

	public static String pathOf(URL url)  {
		return pathOf(url.getPath());
	}

	private static String pathOf(String uriPath) {
		if (uriPath.length() > 2 && uriPath.charAt(2) == ':') {
			uriPath = uriPath.substring(1);
		}
		return uriPath;
	}

	public interface AutoCloseableNoException extends AutoCloseable{
		@Override
		void close();
	}
}
