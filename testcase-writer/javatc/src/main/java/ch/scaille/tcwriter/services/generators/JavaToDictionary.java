package ch.scaille.tcwriter.services.generators;

import static ch.scaille.tcwriter.persistence.factory.DaoConfigs.homeFolder;

import java.util.stream.Collector;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import ch.scaille.generators.util.AbstractGenerator;
import ch.scaille.tcwriter.annotations.TCActors;
import ch.scaille.tcwriter.annotations.TCRole;
import ch.scaille.tcwriter.generators.services.visitors.ClassToDictionaryVisitor;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.persistence.factory.DaoConfigs;
import ch.scaille.util.helpers.ClassFinder;

public class JavaToDictionary extends AbstractGenerator<TestDictionary> {

	private final String classifier;

	public static class Args {
		@Parameter(names = { "-c" }, description = "Name of configuration")
		public String configuration = "default";

		@Parameter(names = { "-s" }, required = true, description = "Source package")
		public String sourcePackage;

		@Parameter(names = { "-s" }, required = true, description = "Name of dictionary")
		public String classifier;
}

	public static Collector<Class<?>, ?, TestDictionary> toDictionary(String classifier) {
		return generate(() -> new JavaToDictionary(classifier));
	}

	protected JavaToDictionary(String classifier) {
		super();
		this.classifier = classifier;
	}

	public JavaToDictionary(String classifier, final Class<?>... tcClasses) {
		super(tcClasses);
		this.classifier = classifier;
	}

	@Override
	public TestDictionary generate() {
		return new ClassToDictionaryVisitor(classifier, classes.toArray(new Class<?>[0])).visit();
	}

	public static void main(final String[] args) {
		final var mainArgs = new Args();
		JCommander.newBuilder().addObject(mainArgs).build().parse(args);
		
		final var daoConfig = DaoConfigs.withFolder(homeFolder());
		daoConfig.configDao().setConfiguration(mainArgs.configuration);
		
		final var persister = daoConfig.modelDao();
		final var dictionary = ClassFinder.ofCurrentThread()
				.withPackages(mainArgs.sourcePackage)
				.withAnnotation(TCRole.class, ClassFinder.Policy.CLASS_ONLY)
				.withAnnotation(TCActors.class, ClassFinder.Policy.CLASS_ONLY)
				.scan()
				.collect(toDictionary(mainArgs.classifier));
		dictionary.getMetadata().setTransientId(mainArgs.classifier);
		persister.writeTestDictionary(dictionary);
	}

}
