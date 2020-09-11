package ch.skymarshall.tcwriter.generators.model.persistence;

import java.io.IOException;
import java.nio.file.Path;

import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

/**
 * Methods used to load/save the configurations, models and test cases
 *
 * @author scaille
 *
 */
public interface IModelPersister {

	void setConfiguration(GeneratorConfig config);

	TestDictionary readTestDictionary() throws IOException;

	void writeTestDictionary(TestDictionary tm) throws IOException;

	void writeTestDictionary(Path target, TestDictionary model) throws IOException;

	TestCase readTestCase(String identifier, TestDictionary testDictionary) throws IOException;

	void writeTestCase(String identifier, TestCase tc) throws IOException;

	GeneratorConfig readConfiguration(String identifier) throws IOException;

	void writeConfiguration(GeneratorConfig config) throws IOException;

}
