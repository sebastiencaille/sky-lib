package ch.skymarshall.tcwriter.generators;

import java.util.List;

import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;

public class ModelFromClassGenerator {
	private final List<Class<?>> tcClasses;

	public ModelFromClassGenerator(final List<Class<?>> tcClasses) {
		this.tcClasses = tcClasses;
	}

	public TestModel generateModel() {

		final TestModel model = new TestModel();
		final ModelFromClassVisitor gen = new ModelFromClassVisitor(model);
		tcClasses.forEach(gen::addClass);
		gen.visit();
		return model;
	}

	public static void main(final String[] args) {
		final ModelFromClassGenerator generateFromCode = new ModelFromClassGenerator(Helper.toClasses(args));
		final TestModel model = generateFromCode.generateModel();
		System.out.println(Helper.dumpModel(model));
	}

}
