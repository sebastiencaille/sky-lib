package ch.scaille.tcwriter.generators;

import java.io.IOException;
import java.util.stream.Collector;

import ch.scaille.generators.util.AbstractGenerator;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.generators.model.persistence.IModelPersister;
import ch.scaille.tcwriter.generators.model.persistence.JsonModelPersister;
import ch.scaille.tcwriter.generators.model.testapi.TestDictionary;
import ch.scaille.tcwriter.generators.visitors.ClassToDictionaryVisitor;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.ClassFinder.Policy;

public class JavaToDictionary extends AbstractGenerator<TestDictionary> {

	public static Collector<Class<?>, ?, TestDictionary> toDictionary() {
		return toDictionary(JavaToDictionary::new);
	}
	
	protected JavaToDictionary() {
		super();
	}

	public JavaToDictionary(final Class<?>... tcClasses) {
		super(tcClasses);
	}

	@Override
	public TestDictionary generate() {

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
		final TestDictionary dictionary = ClassFinder.forApp().withPackages(sourcePackage).)
				.withAnnotation(TCRole.class, Policy.CLASS_ONLY).withAnnotation(TCActors.class, Policy.CLASS_ONLY)
				.scan().collect(JavaToDictionary.toDictionary());
		persister.writeTestDictionary(dictionary);
	}

}
