package ch.scaille.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.helpers.ClassFinder;

class ClassFinderTest {

	public static class TestClassFinder extends ClassFinder {
		protected TestClassFinder(URL[] urls) {
			super(urls);
		}

		public class TestFsScanner extends FsScanner {
			@Override
			protected String packageLocationOf(URI uri, String aPackage) {
				return super.packageLocationOf(uri, aPackage);
			}

			@Override
			public URI rootOf(URI uri) {
				return super.rootOf(uri);
			}

		}

		public TestFsScanner scanner() {
			return new TestFsScanner();
		}
	}

	@Test
	void testWindows() throws MalformedURLException, URISyntaxException {
		TestClassFinder.TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL("file:/C:/toto/mypackage").toURI();
		Assertions.assertEquals(URI.create("file:/"), scanner.rootOf(testUri));
		Assertions.assertEquals("C:/toto", scanner.packageLocationOf(testUri, "mypackage"));
	}

	@Test
	void testUnix() throws MalformedURLException, URISyntaxException {
		TestClassFinder.TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL("file:/toto/mypackage").toURI();
		Assertions.assertEquals(URI.create("file:/"), scanner.rootOf(testUri));
		Assertions.assertEquals("/toto", scanner.packageLocationOf(testUri, "mypackage"));
	}

	@Test
	void testJarPath() throws MalformedURLException, URISyntaxException {
		TestClassFinder.TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL("jar:file:/toto.jar!/mypackage").toURI();
		Assertions.assertEquals(URI.create("jar:file:/toto.jar"), scanner.rootOf(testUri));
		Assertions.assertEquals("/", scanner.packageLocationOf(testUri, "mypackage"));
	}

	@Test
	void testScan() {
		Assertions.assertEquals(1, ClassFinder.forApp().withPackages("ch.scaille.util").scan()
				.filter(c -> c.equals(ClassFinder.class)).count());
	}
}
