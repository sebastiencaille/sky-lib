package ch.skymarshall.tcwriter.generators.model.persistence;

import java.io.IOException;
import java.nio.file.Path;

import ch.skymarshall.tcwriter.generators.GeneratorConfig;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.model.testcase.TestCase;

public interface IModelPersister {

	void setConfiguration(GeneratorConfig config);

	TestModel readTestModel() throws IOException;

	void writeTestModel(TestModel tm) throws IOException;

	void writeTestModel(Path target, TestModel model) throws IOException;

	TestCase readTestCase(String identifier, TestModel testModel) throws IOException;

	void writeTestCase(String identifier, TestCase tc) throws IOException;

	GeneratorConfig readConfiguration(String identifier) throws IOException;

	void writeConfiguration(GeneratorConfig config) throws IOException;

}
