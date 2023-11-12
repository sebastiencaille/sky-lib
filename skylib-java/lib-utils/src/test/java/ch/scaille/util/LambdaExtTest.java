package ch.scaille.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.LambdaExt;

class LambdaExtTest {

	private static final String STR = "https://www.google.com";

	private URL url() throws MalformedURLException {
		return new URL(STR);
	}

	private URL str2url(String url) throws MalformedURLException {
		return new URL(url);
	}

	private void consumeUrl(String u) {
		// noop
	}

	@Test
	void testCompile() throws MalformedURLException {
		LambdaExt.uncheckedR(this::url);
		Optional.of(STR).ifPresent(LambdaExt.uncheckedC(u -> consumeUrl(u)));
		Assertions.assertEquals(url(), LambdaExt.uncheck(() -> url()));
		Assertions.assertEquals(Optional.of(url()), Optional.of(STR).map(LambdaExt.uncheckedF(u -> url())));

		LambdaExt.uncheckedR(this::url);
		Optional.of(STR).ifPresent(LambdaExt.uncheckedC(this::consumeUrl));
		Assertions.assertEquals(url(), LambdaExt.uncheck(this::url));
		Assertions.assertEquals(Optional.of(url()), Optional.of(STR).map(LambdaExt.uncheckedF(this::str2url)));
	}

	@Test()
	void testExceptionRaised() {
		Assertions.assertThrows(IllegalStateException.class, () -> LambdaExt.uncheck(() -> {
			throw new IOException("Huh");
		}));
	}
}
