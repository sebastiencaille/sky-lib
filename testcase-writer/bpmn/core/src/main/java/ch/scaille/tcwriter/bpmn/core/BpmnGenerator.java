package ch.scaille.tcwriter.bpmn.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import ch.scaille.tcwriter.model.NamedObject;
import ch.scaille.tcwriter.model.dictionary.ParameterNature;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.dictionary.TestRole;
import ch.scaille.util.text.TextFormatter;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public abstract class BpmnGenerator {

	public static final String ACTION = "action";

	public record AppModel(String key, String type) {
	}

	public record TestDictionaryMetadata(String id, String name, String description) {
	}

	public record TestCaseMetadata(String name, String className, String description, String[] tags) {
	}

	public record Action(String key, String name, String role, String action, String formKey) {
	}

	public record ActionParam(String testApiParameter, ParameterNature nature, Map<String, String> parameterMappings) {
	}

	public record ActionForm(String formKey, String modelKey, String name, String description, int version,
	                                 List<TestApiParameterForm> parameters,
									 Map<String, String> actors) {
	}

    public record TestApiParameterForm(String label, List<TestParameterFactoryForm> parameters) {
	}

	public record TestParameterFactoryForm(String formKey, String modelKey, String name, String description, int version,
												   List<ParameterFactoryParameterForm> parameters) {
	}

	public record ParameterFactoryParameterForm(String id, String label, String variable, boolean required) {
	}


    private final String flavor;
    protected final TestDictionary dictionary;
	protected final Path outputFolder;

	protected BpmnGenerator(String flavor, TestDictionary dictionary, Path outputFolder) {
        this.flavor = flavor;
        this.dictionary = dictionary;
		this.outputFolder = outputFolder;
	}


	protected ActionForm createActionFormMetadata(TestAction action, TestRole role) {
		return new ActionForm(actionFormOf(action), action.getName(), shortNameOf(action),
				dictionary.descriptionOf(action).description(), 0,
				action.getParameters().stream().map(this::createActionParameterFormMetadata).toList(),
				dictionary.getActors().values().stream().filter(actor -> actor.getRole().equals(role)).collect(
						Collectors.toMap(TestActor::getId, actor -> dictionary.descriptionOf(actor).description())));
	}

	public TestApiParameterForm createActionParameterFormMetadata(TestApiParameter apiParameter) {
		Objects.requireNonNull(apiParameter);
		var factories = dictionary.getTestObjectFactories().get(apiParameter.getParameterType());
		if (factories.isEmpty()) {
			factories = List.of(apiParameter.asSimpleParameter());
		}
		return new TestApiParameterForm(dictionary.descriptionOf(apiParameter).description(),
			factories.stream()
				.map(factory ->
						new TestParameterFactoryForm("PARAM-FACTORY-" + shortNameOf(factory), factory.getId(),
								"ParameterFactory-" + shortNameOf(factory), dictionary.descriptionOf(factory).description(), 0,
								parameterFactoryParameters(apiParameter, factory, true))).toList());
	}

	private List<ParameterFactoryParameterForm> parameterFactoryParameters(TestApiParameter owner, TestParameterFactory factory, boolean mandatory) {
		return switch (factory.getNature()) {
			case SIMPLE_TYPE -> List.of(new ParameterFactoryParameterForm(owner.getId(), dictionary.descriptionOf(owner).description(),
					variableNameOf(owner), mandatory));
			case TEST_API -> Streams.concat(
							factory.getMandatoryParameters().stream()
									.flatMap(param -> parameterFactoryParameters(param, param.asSimpleParameter(), true).stream()),
							factory.getOptionalParameters().stream()
									.flatMap(param -> parameterFactoryParameters(param, param.asSimpleParameter(), false).stream()))
					.toList();
			default -> throw new NotImplementedException("Not implemented: " + factory.getNature());
		};
	}

	protected void generateFile(VelocityEngine engine, VelocityContext context, String template, String outputFile) {
		try (var writer = Files.newBufferedWriter(outputFolder.resolve(outputFile))) {

			engine.getTemplate(Paths.get("templates", flavor, template).toString()).merge(context, writer);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to write form " + outputFile, e);
		}
	}

	protected void generateFile(VelocityEngine engine, Map<String, Object> contextMap, String template,
			String outputFile) {
		this.generateFile(engine, new VelocityContext(new HashMap<>(contextMap)), template, outputFile);
	}

	protected String actionFormOf(TestAction action) {
		return "ACTION-FORM-" + shortNameOf(action);
	}

	public void generateInit(VelocityEngine engine) {
		final var metadata = new TestCaseMetadata("BpmnTest1", "ch.scaille.tcwriter." + flavor + "BpmnTest",
				"A bpmn version of the test example (" + flavor + " flavor)",
				new String[] { dictionary.getMetadata().getTags().iterator().next() });
		generateFile(engine, Map.of("testCase", metadata), "TCW-INIT.bpmn.vm", "TCW-INIT.bpmn");
	}

	public void generateActionBpmn(VelocityEngine engine, TestRole testRole, TestAction testAction) {
		final var action = new Action("ACTION_" + shortNameOf(testAction),
				dictionary.descriptionOf(testAction).description(), testRole.getId(), testAction.getId(),
				actionFormOf(testAction));

		final var context = new VelocityContext(new HashMap<>(Map.of("action", action)));

		if (!testAction.getParameters().isEmpty()) {
			context.put("param0", buildActionParam(testAction.getParameter(0)));
		}
		if (testAction.getParameters().size() > 1) {
			context.put("param1", buildActionParam(testAction.getParameter(1)));
		}
		generateFile(engine, context, "Action.bpmn.vm", action.key() + ".bpmn");
	}

	private ActionParam buildActionParam(TestApiParameter param) {
		var parameterFactories = dictionary.getParameterFactories(param);
		var nature = ParameterNature.TEST_API;
		if (parameterFactories.isEmpty()) {
			parameterFactories = List.of(param.asSimpleParameter());
			nature = ParameterNature.SIMPLE_TYPE;
		}
		final var paramParameters = new HashMap<>(parameterFactories.stream()
				.flatMap(factory -> Streams.concat(factory.getMandatoryParameters().stream(),
						factory.getOptionalParameters().stream()))
				.collect(Collectors.toMap(TestApiParameter::getId, TestApiParameter::getParameterType, (v1, _) -> v1)));
		// Mapping variable -> id
		paramParameters.putAll(parameterFactories.stream()
				.flatMap(factory -> Streams.concat(factory.getMandatoryParameters().stream(),
						factory.getOptionalParameters().stream()))
				.collect(Collectors.toMap(this::variableNameOf, TestApiParameter::getId, (v1, _) -> v1)));
		return new ActionParam(param.getId(), nature, paramParameters);
	}

	protected String shortNameOf(NamedObject object) {
		return object.getName().substring(object.getName().lastIndexOf('.') + 1);
	}

	protected String variableNameOf(TestApiParameter parameter) {
		return TextFormatter.snakeCaseToCamelCase(parameter.getId().replaceAll("[-.]", "_"));
	}

	protected void generateAction(VelocityEngine engine, TestRole testRole, TestAction testAction) {
		generateActionBpmn(engine, testRole, testAction);
	}

	public VelocityEngine generate() throws IOException {
		Files.createDirectories(outputFolder);

		final var props = new Properties();
		props.setProperty("resource.loader", "classpath");
		props.setProperty("classpath.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		final var engine = new VelocityEngine(props);
		engine.init();

		generateInit(engine);

		for (var testRole : dictionary.getRoles().values()) {
			for (var testAction : testRole.getActions()) {
				generateAction(engine, testRole, testAction);
			}
		}
		generateFile(engine, new VelocityContext(), "TCW-PUBLISH.bpmn", "TCW-PUBLISH.bpmn");
		return engine;
	}

}