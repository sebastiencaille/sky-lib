package ch.skymarshall.tcwriter.generators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import ch.skymarshall.tcwriter.generators.model.testapi.TestModel;
import ch.skymarshall.tcwriter.generators.visitors.ClassToModelVisitor;
import ch.skymarshall.tcwriter.generators.visitors.JsonHelper;

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

		final String target = args[0];
		final String[] roleClasses = new String[args.length - 1];
		System.arraycopy(args, 0, roleClasses, 0, roleClasses.length);
		final TestModel model = new JavaToModel(Helper.toClasses(roleClasses)).generateModel();
		Files.write(new File(target).toPath(), JsonHelper.toJson(model).getBytes(StandardCharsets.UTF_8));

	}

}
