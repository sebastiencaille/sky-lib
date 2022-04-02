package ch.scaille.tcwriter.generators;

import java.io.IOException;
import java.util.stream.Collector;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.generators.util.AbstractGenerator;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.generators.visitors.ClassToDictionaryVisitor;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.tcwriter.model.testapi.TestDictionary;
import ch.scaille.util.helpers.ClassFinder;

public class JavaToDictionary extends AbstractGenerator<TestDictionary> {
	public static class Args {
		@Parameter(names = { "-c" }, required = false, description = "Name of configuration")
		public String configuration;

		@Parameter(names = { "-s" }, required = true, description = "Source package")
		public String sourcePackage;
	}

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
		return new ClassToDictionaryVisitor(classes.toArray(new Class<?>[0])).visit();
	}

	public static void main(final String[] args) throws IOException {
		var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		var persister = FsModelDao.withDefaultConfig();
		if (mainArgs.configuration != null) {
			persister.loadConfiguration(mainArgs.configuration);
		}
		var dictionary = ClassFinder.ofCurrentThread().withPackages(mainArgs.sourcePackage)
				.withAnnotation(TCRole.class, ClassFinder.Policy.CLASS_ONLY)
				.withAnnotation(TCActors.class, ClassFinder.Policy.CLASS_ONLY).scan().collect(toDictionary());
		persister.writeTestDictionary(dictionary);
	}

}
