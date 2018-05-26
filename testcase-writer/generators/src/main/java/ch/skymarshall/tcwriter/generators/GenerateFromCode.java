package ch.skymarshall.tcwriter.generators;

import java.util.List;

import ch.skymarshall.tcwriter.generators.model.TestModel;

public class GenerateFromCode {
	private final List<Class<?>> tcClasses;

	public GenerateFromCode(final List<Class<?>> tcClasses) {
		this.tcClasses = tcClasses;
	}

	public TestModel generateModel() {

		final TestModel model = new TestModel();
		final ModelFromCodeGenerator gen = new ModelFromCodeGenerator(model);
		tcClasses.forEach(gen::addClass);
		gen.visit();
		return model;
	}

	public static void main(final String[] args) throws ClassNotFoundException {
		final GenerateFromCode generateFromCode = new GenerateFromCode(Helper.toClasses(args));
		final TestModel model = generateFromCode.generateModel();
		Helper.dumpModel(model);
	}

}
