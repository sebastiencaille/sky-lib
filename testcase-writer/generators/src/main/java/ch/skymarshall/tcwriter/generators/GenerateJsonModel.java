package ch.skymarshall.tcwriter.generators;

import ch.skymarshall.tcwriter.generators.model.TestActor;
import ch.skymarshall.tcwriter.generators.model.TestModel;
import ch.skymarshall.tcwriter.generators.model.TestObject;

public class GenerateJsonModel {
	public static void main(final String[] args) throws ClassNotFoundException {

		final String[] roleClasses = args;

		final TestModel model = new TestModel();
		final JsonModelGenerator gen = new JsonModelGenerator(model);
		for (final String roleClassName : roleClasses) {
			gen.addClass(Class.forName(roleClassName));

		}
		gen.visit();
		System.out.println(model.toString());
		for (final TestActor actor : model.getActors().values()) {
			System.out.println("  " + model.getDescriptions().get(actor.getId()) + ": " + actor);
			actor.getApis()
					.forEach(api -> System.out.println("    " + model.getDescriptions().get(api.getId()) + ": " + api));
		}
		for (final TestObject testObject : model.getTestObjects().values()) {
			System.out.println("  " + model.getDescriptions().get(testObject.getId()) + ": " + testObject);
			testObject.getMandatoryParameters().forEach(api -> System.out
					.println("    mandatory: " + model.getDescriptions().get(api.getId()) + ": " + api));
			testObject.getOptionalParameters().forEach(api -> System.out
					.println("    optional: " + model.getDescriptions().get(api.getId()) + ": " + api));
		}
	}
}
