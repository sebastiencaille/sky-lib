package ch.scaille.util.helpers.protocols.rsrc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		final var all = Thread.currentThread().getContextClassLoader().getResources(url.getPath());
		if (!all.hasMoreElements()) {
			throw new FileNotFoundException("Resource not found: " + url);
		}
		final var found = all.nextElement();
		if (all.hasMoreElements()) {
			throw new FileNotFoundException(
					"Too many resources found: " + url + ": " + url + " and " + all.nextElement());
		}
		return found.openConnection();
	}

}