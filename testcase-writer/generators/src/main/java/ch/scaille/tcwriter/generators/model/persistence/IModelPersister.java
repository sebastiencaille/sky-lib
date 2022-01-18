package ch.scaille.tcwriter.generators.model.persistence;

import java.io.IOException;
import java.nio.file.Path;

import ch.scaille.generators.util.Template;
import ch.scaille.tcwriter.generators.TCConfig;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.model.testcase.TestCase;

/**
 * Methods used to load/save the configurations, models and test cases
 *
 * @author scaille
 *
 */
public interface IModelPersister {

	TCConfig getConfiguration();
	
	void setConfiguration(TCConfig config);

	TestDictionary readTestDictionary() throws IOException;

	void writeTestDictionary(TestDictionary tm) throws IOException;

	void writeTestDictionary(Path target, TestDictionary model) throws IOException;

	TestCase readTestCase(String identifier, TestDictionary testDictionary) throws IOException;

	void writeTestCase(String identifier, TestCase tc) throws IOException;

	TCConfig readConfiguration(String identifier) throws IOException;

	void writeConfiguration(TCConfig config) throws IOException;

	Template readTemplate() throws IOException;

	Path getExportedTCPath();

}
