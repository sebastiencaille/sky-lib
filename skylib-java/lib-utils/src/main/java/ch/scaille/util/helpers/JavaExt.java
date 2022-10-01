package ch.scaille.util.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	public static String pathOf(URL url) {
		return pathOf(url.getPath());
	}

	private static String pathOf(String uriPath) {
		if (uriPath.length() > 2 && uriPath.charAt(2) == ':') {
			uriPath = uriPath.substring(1);
		}
		return uriPath;
	}

	public interface AutoCloseableNoException extends AutoCloseable {
		@Override
		void close();
	}

	public static <T extends OutputStream> T transferTo(InputStream in, T out) throws IOException {
		final byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		return out;
	}
	
	public static byte[] read(InputStream in) throws IOException {
		return transferTo(in, new ByteArrayOutputStream()).toByteArray();
	}
}
