package ch.skymarshall.tcwriter.generators;

import java.io.IOException;
import java.util.Collection;

import ch.skymarshall.tcwriter.generators.model.persistence.IModelPersister;
import ch.skymarshall.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.skymarshall.tcwriter.generators.model.testapi.TestDictionary;
import ch.skymarshall.tcwriter.generators.visitors.ClassToDictionaryVisitor;
import ch.skymarshall.util.helpers.ClassFinder;

public class JavaToDictionary {

	private final Collection<Class<?>> tcClasses;

	public JavaToDictionary(final Collection<Class<?>> tcClasses) {
		this.tcClasses = tcClasses;
	}

	public TestDictionary generateDictionary() {

		final TestDictionary model = new TestDictionary();
		final ClassToDictionaryVisitor gen = new ClassToDictionaryVisitor(model);
		tcClasses.forEach(gen::addClass);
		gen.visit();
		return model;
	}

	public static void main(final String[] args) throws IOException {

		final IModelPersister persister = new JsonModelPersister();
		persister.setConfiguration(persister.readConfiguration(args[0]));

		final String sourcePackage = args[0];
		final TestDictionary dictionary = new JavaToDictionary(ClassFinder.forApp().collect(sourcePackage).getResult())
				.generateDictionary();
		persister.writeTestDictionary(dictionary);
	}

}
