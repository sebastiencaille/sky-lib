package ch.scaille.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

	@ParameterizedTest
	@CsvSource({ //
			"file:/C:/toto/mypackage,file:/,C:/toto", // windows
			"file:/toto/mypackage,file:/,/toto", // unix 
			"jar:file:/toto.jar!/mypackage,jar:file:/toto.jar,/" // jar
			})
	void testUriTransformation(String urlPackageLocation, String fsRoot, String fsPackageLocation)
			throws MalformedURLException, URISyntaxException {
		TestClassFinder.TestFsScanner scanner = new TestClassFinder(new URL[0]).scanner();
		URI testUri = new URL(urlPackageLocation).toURI();
		Assertions.assertEquals(URI.create(fsRoot), scanner.rootOf(testUri));
		Assertions.assertEquals(fsPackageLocation, scanner.packageLocationOf(testUri, "mypackage"));
	}

	@Test
	void testScan() {
		Assertions.assertEquals(1, ClassFinder.forApp().withPackages("ch.scaille.util").scan()
				.filter(c -> c.equals(ClassFinder.class)).count());
	}
}
