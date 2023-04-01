package ch.scaille.tcwriter.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface IResourceLoader {

	Stream<String> list() throws IOException;

	String read(String locator) throws IOException;

	String read(Path path) throws IOException;

	String write(String locator, String value) throws IOException;

	String write(Path path, String value) throws IOException;

	Path getBaseFolder();


}
