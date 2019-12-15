package ch.skymarshall.tcwriter.generators;

import java.io.IOException;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.visitors.ClassToModelVisitor;

public class JavaToModel {

	private final List<Class<?>> tcClasses;

	public JavaToModel(final List<Class<?>> tcClasses) {
		this.tcClasses = tcClasses;
	}

	public TestModel generateModel() {

		final TestModel model = new TestModel();
		final ClassToModelVisitor gen = new ClassToModelVisitor(model);
		tcClasses.forEach(gen::addClass);
		gen.visit();
		return model;
	}

	public static void main(final String[] args) throws IOException {

		final IModelPersister persister = new JsonModelPersister();
		persister.setConfiguration(persister.readConfiguration(args[0]));
		final String[] roleClasses = new String[args.length - 1];
		System.arraycopy(args, 0, roleClasses, 0, roleClasses.length);
		final TestModel model = new JavaToModel(Helper.toClasses(roleClasses)).generateModel();
		persister.writeTestModel(model);

	}

}
