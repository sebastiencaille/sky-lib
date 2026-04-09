package ch.scaille.tcwriter.flow.flowable;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ch.scaille.tcwriter.model.NamedObject;
import ch.scaille.tcwriter.model.dictionary.ParameterNature;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestActor;
import ch.scaille.tcwriter.model.dictionary.TestApiParameter;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestParameterFactory;
import ch.scaille.tcwriter.model.dictionary.TestRole;
import com.google.common.collect.Streams;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FlowGenerator {

    public static final String METADATA = "metadata";

    // Inner class for form items
    public record FormMetadata(
            String modelKey,
            String tcwKey,
            String name,
            String description,
            int version) {
    }

    public record ActionForm(
            String param0Label,
            String param1Label,
            Map<String, String> actors) {
    }

    // Inner class for form items
    public record FormItem(
            String text,
            String value,
            String formRef,
            String id) {
    }

    // Inner class for form items

    public record SubFormItem(
            String id,
            String label,
            String variable,
            boolean required) {
    }

    public record TestDictionaryMetadata(
            String id,
            String name,
            String description) {
    }

    public record TestCaseMetadata(
            String name,
            String className,
            String description,
            String[] tags) {
    }

    public record Action(
            String key,
            String name,
            String role,
            String action,
            String formKey) {
    }
    public record ActionParam (
            String testApiParameter,
            ParameterNature nature,
            Map<String, String> parameterMappings) {
    }

    public record AppModel (
            String key,
            String type) {
    }

    private final TestDictionary dictionary;
    private final Path outputFolder;

    public FlowGenerator(TestDictionary dictionary, Path outputFolder) {
        this.dictionary = dictionary;
        this.outputFolder = outputFolder;
    }
    
    private void generateFile(VelocityEngine engine, Map<String, Object> contextMap, String template, String outputFile) {
        this.generateFile(engine, new VelocityContext(new HashMap<>(contextMap)), template, outputFile);
    }
    
    private void generateFile(VelocityEngine engine, VelocityContext context, String template, String outputFile) {
        try (var writer = Files.newBufferedWriter(outputFolder.resolve(outputFile))) {

            engine.getTemplate("templates/flowable/" + template).merge(context, writer);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write form " + outputFile, e);
        }
    }
    
    public List<FormMetadata> generateSubForm(VelocityEngine engine, TestApiParameter apiParameter) {

        var factories = dictionary.getTestObjectFactories().get(apiParameter.getParameterType());
        if (factories.isEmpty()) {
            factories = List.of(apiParameter.asSimpleParameter());
        }
        return factories.stream()
                .map(factory ->
                        generateSubForm(engine,
                                new FormMetadata("SUBFORM-FACTORY-" + shortNameOf(factory),
                                        factory.getId(),
                                        "SubForm-" + shortNameOf(factory),
                                        dictionary.descriptionOf(factory).description(),
                                        0),
                                subFormItem(apiParameter, factory, true)))
                .toList();
    }

    private List<SubFormItem> subFormItem(TestApiParameter owner, TestParameterFactory factory, boolean mandatory) {
        return switch (factory.getNature()) {
            case SIMPLE_TYPE -> List.of(new SubFormItem(owner.getId(), dictionary.descriptionOf(owner).description(),
                    "{{" + variableNameOf(owner) + "}}", mandatory));
            case TEST_API ->
                    Streams.concat(factory.getMandatoryParameters().stream()
                                            .flatMap(param -> subFormItem(param, param.asSimpleParameter(), true).stream()), 
                                    factory.getOptionalParameters().stream()
                                            .flatMap(param -> subFormItem(param, param.asSimpleParameter(), false).stream())
                            ).toList();
            default -> throw new NotImplementedException("Not implemented: " + factory.getNature());
        };
    }

    private FormMetadata generateSubForm(VelocityEngine engine, FormMetadata metadata, List<SubFormItem> rows) {
        generateFile(engine, Map.of(
                "metadata", metadata,
                "rows", rows
        ), "TestParameterFactoryForm.form.vm", metadata.modelKey() + ".form");
        return metadata;
    }


    private void generateActionForm(VelocityEngine engine, FormMetadata metadata, ActionForm actionForm,
                                            @Nullable TestApiParameter param0, List<FormMetadata> param0List,
                                            @Nullable TestApiParameter param1, List<FormMetadata> param1List) {
        final var context = new VelocityContext(new HashMap<>(Map.of(
                METADATA, metadata,
                "form", actionForm
        )));
        if (param0 != null) {
            context.put("param0", param0List.stream().map(subForm -> new FormItem(
                    subForm.description(),
                    subForm.tcwKey(),
                    subForm.modelKey(),
                    subForm.modelKey())).toList());
        }
        if (param1 != null) {
            context.put("param1", param1List.stream().map(subForm -> new FormItem(
                    subForm.description(),
                    subForm.tcwKey(),
                    subForm.modelKey(),
                    subForm.modelKey())).toList());
        }
        generateFile(engine, context, "ActionForm.form.vm", metadata.modelKey() + ".form");
    }


    public void generateActionForm(VelocityEngine engine, TestRole role, TestAction action) {

        final var metadata = new FormMetadata(actionFormOf(action),
                action.getName(),
                shortNameOf(action),
                dictionary.descriptionOf(action).description(),
                0);

        TestApiParameter param0 = null;
        var param0Label = "";
        var param0List = new ArrayList<FormMetadata>();
        if (!action.getParameters().isEmpty()) {
            param0 = action.getParameter(0);
            param0Label = dictionary.descriptionOf(param0).description();
            param0List.addAll(generateSubForm(engine, param0));
        }
        
        TestApiParameter param1 = null;
        var param1Label = "";
        final var param1List = new ArrayList<FormMetadata>();
        if (action.getParameters().size() > 1) {
            param1 = action.getParameter(1);
            param1Label = dictionary.descriptionOf(action.getParameter(1)).description();
            param1List.addAll(generateSubForm(engine, action.getParameter(1)));
        }

        generateActionForm(engine, metadata, new ActionForm(param0Label, param1Label, 
                        dictionary.getActors().values().stream()
                                .filter(actor -> actor.getRole().equals(role))
                                .collect(Collectors.toMap(TestActor::getId, actor -> dictionary.descriptionOf(actor).description()))),
                param0, param0List,
                param1, param1List);
    }

    private String actionFormOf(TestAction action) {
        return "ACTION-FORM-" + shortNameOf(action);
    }

    public void generateInit(VelocityEngine engine) {
        final var metadata = new TestCaseMetadata(
        "FlowableTest1",
        "ch.scaille.tcwriter.flowableTest",
        "A test generated using Flowable",
                new String[] { dictionary.getMetadata().getTags().iterator().next()});
        generateFile(engine, Map.of("testCase", metadata), "TCW-INIT.bpmn.vm", "TCW-INIT.bpmn");
    }

    public void generateAction(VelocityEngine engine, TestRole testRole, TestAction testAction) {
        final var action = new Action(
                "ACTION_" + shortNameOf(testAction),
                dictionary.descriptionOf(testAction).description(),
                testRole.getId(),
                testAction.getId(),
                actionFormOf(testAction));

        final var context = new VelocityContext(new HashMap<>(Map.of(
            "action", action
        )));
        
        if (!testAction.getParameters().isEmpty()) {
            context.put("param0", buildActionParam(testAction.getParameter(0)));
        }
        if (testAction.getParameters().size() > 1) {
            context.put("param1", buildActionParam(testAction.getParameter(1)));
        }
        generateFile(engine, context,
                "Action.bpmn.vm", action.key() + ".bpmn");
    }

    private ActionParam buildActionParam(TestApiParameter param) {
        var parameterFactories = dictionary.getParameterFactories(param);
        var nature = ParameterNature.TEST_API;
        if (parameterFactories.isEmpty()) {
            parameterFactories = List.of(param.asSimpleParameter());
            nature = ParameterNature.SIMPLE_TYPE;
        }
        final var paramParameters = new HashMap<>(parameterFactories.stream()
                .flatMap(factory -> Streams.concat(factory.getMandatoryParameters().stream(), factory.getOptionalParameters().stream()))
                .collect(Collectors.toMap(TestApiParameter::getId, TestApiParameter::getParameterType, (v1, _) -> v1)));
        // Mapping variable -> id
        paramParameters.putAll(parameterFactories.stream()
                .flatMap(factory -> Streams.concat(factory.getMandatoryParameters().stream(), factory.getOptionalParameters().stream()))
                .collect(Collectors.toMap(this::variableNameOf, TestApiParameter::getId, (v1, _) -> v1)));
        return new ActionParam(
                param.getId(),
                nature,
                paramParameters);
    }

    private String shortNameOf(NamedObject object) {
        return object.getName().substring(object.getName().lastIndexOf('.') + 1);
    }

    private String variableNameOf(TestApiParameter parameter) {
        return parameter.getId().replaceAll("[-.]", "_");
    }

    public void generate() throws IOException {
        Files.createDirectories(outputFolder);

        final var props = new Properties();
        props.setProperty("resource.loader", "classpath");
        props.setProperty("classpath.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        
        final var engine = new VelocityEngine(props);
        engine.init();
        
        generateInit(engine);
        
        for (var testRole : dictionary.getRoles().values()) {
            for (var testAction: testRole.getActions()) {
                generateAction(engine, testRole, testAction);
                generateActionForm(engine, testRole, testAction);
            }
        }
        generateFile(engine, new VelocityContext(), "TCW-PUBLISH.bpmn", "TCW-PUBLISH.bpmn");
        generateFile(engine, new VelocityContext(), "service-TCW-REST-API.service", "service-TCW-REST-API.service");
        generateFile(engine, new VelocityContext(), "template-TCW-BODY-JSON.tpl", "template-TCW-BODY-JSON.tpl");
        
        try (final var files = Files.list(outputFolder)) {
            final var appContext = new VelocityContext(new HashMap<>(Map.of(
                    "metadata", new TestDictionaryMetadata("TCW-SIMPLE-EXAMPLE", "TCW-SIMPLE-EXAMPLE", "Simple example"),
                    "childModels", files
                        .map(f -> f.getFileName().toString().split("\\."))
                                    .map(split -> {
                                        if ("tpl".equals(split[1])) {
                                            return new AppModel(split[0].replace("template-", ""), "template");
                                        }
                                        return new AppModel(split[0], split[1]);
                                    })
                        .toList())));
            generateFile(engine, appContext, "TCWM.app.vm", "TCWM.app");
        }

        try (var zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFolder+ ".zip")));
            final var files = Files.list(outputFolder)) {
            for (var file: files.toList()) {
                zip.putNextEntry(new ZipEntry(file.getFileName().toString()));
                zip.write(Files.readAllBytes(file));
            }
        }
    }

}