package ch.scaille.tcwriter.generators;

import java.util.stream.Collector;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.generators.util.AbstractGenerator;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.config.FsConfigManager;
import ch.scaille.tcwriter.generators.visitors.ClassToDictionaryVisitor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.persistence.FsModelDao;
import ch.scaille.util.helpers.ClassFinder;

public class JavaToDictionary extends AbstractGenerator<TestDictionary> {
	
	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration = "default";

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

	public static void main(final String[] args) {
		var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		var configManager = FsConfigManager.local().setConfiguration("default");
		var persister = new FsModelDao(configManager);
		var dictionary = ClassFinder.ofCurrentThread().withPackages(mainArgs.sourcePackage)
				.withAnnotation(TCRole.class, ClassFinder.Policy.CLASS_ONLY)
				.withAnnotation(TCActors.class, ClassFinder.Policy.CLASS_ONLY).scan().collect(toDictionary());
		persister.writeTestDictionary(dictionary);
	}

}
