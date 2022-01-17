package ch.scaille.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ch.scaille.util.ClassFinderTest.TestClassFinder.TestFsScanner;
import ch.scaille.util.helpers.ClassFinder;

class ClassFinderTest {

	public static class TestClassFinder extends ClassFinder {
		protected TestClassFinder(URL[] urls) {
			super(urls);
		}

		public class TestFsScanner extends FsScanner {
			@Override
			protected String rootOfPackage(URI uri, String aPackage) {
				return super.rootOfPackage(uri, aPackage);
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
	void testWin32Path() throws MalformedURLException, URISyntaxException {
		TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL("file:/C:/toto/mypackage").toURI();
		Assertions.assertEquals(URI.create("file:/"), scanner.rootOf(testUri));
		Assertions.assertEquals("C:/toto", scanner.rootOfPackage(testUri, "mypackage"));
	}
	
	@Test
	void testUnix() throws MalformedURLException, URISyntaxException {
		TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL("file:/toto/mypackage").toURI();
		Assertions.assertEquals(URI.create("file:/"), scanner.rootOf(testUri));
		Assertions.assertEquals("/toto", scanner.rootOfPackage(testUri, "mypackage"));
	}
	
	@Test
	void testJarPath() throws MalformedURLException, URISyntaxException {
		TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL("jar:file:/toto.jar!/mypackage").toURI();
		Assertions.assertEquals(URI.create("jar:file:/toto.jar"), scanner.rootOf(testUri));
		Assertions.assertEquals("/", scanner.rootOfPackage(testUri, "mypackage"));
	}

}
