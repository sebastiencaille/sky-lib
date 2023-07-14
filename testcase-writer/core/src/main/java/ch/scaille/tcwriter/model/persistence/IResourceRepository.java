package ch.scaille.tcwriter.model.persistence;

import java.io.IOException;
import java.util.stream.Stream;

public interface IResourceRepository {

	Stream<String> list() throws IOException;

	Resource read(String locator) throws IOException;

	String write(String locator, String value) throws IOException;

}
