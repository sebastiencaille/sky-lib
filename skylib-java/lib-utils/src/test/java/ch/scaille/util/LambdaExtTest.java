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
		LambdaExt.uncheckR(this::url);
		Optional.of(STR).ifPresent(LambdaExt.uncheckC(u -> consumeUrl(u)));
		Assertions.assertEquals(url(), LambdaExt.uncheckM(() -> url()));
		Assertions.assertEquals(Optional.of(url()), Optional.of(STR).map(LambdaExt.uncheckF(u -> url())));

		LambdaExt.uncheckR(this::url);
		Optional.of(STR).ifPresent(LambdaExt.uncheckC(this::consumeUrl));
		Assertions.assertEquals(url(), LambdaExt.uncheckM(this::url));
		Assertions.assertEquals(Optional.of(url()), Optional.of(STR).map(LambdaExt.uncheckF(this::str2url)));
	}

	@Test()
	void testExceptionRaised() {
		Assertions.assertThrows(IllegalStateException.class, () -> LambdaExt.uncheckM(() -> {
			throw new IOException("Huh");
		}));
	}
}
