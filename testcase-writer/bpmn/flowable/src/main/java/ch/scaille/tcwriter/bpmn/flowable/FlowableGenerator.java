package ch.scaille.tcwriter.bpmn.flowable;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ch.scaille.tcwriter.bpmn.core.BpmnGenerator;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestRole;

public class FlowableGenerator extends BpmnGenerator {

	public FlowableGenerator(TestDictionary dictionary, Path outputFolder) {
		super("flowable", dictionary, outputFolder);
	}

	public void generateTestApiParameterForms(VelocityEngine engine, TestApiParameterForm metadata) {
		metadata.parameters().forEach(factory -> generateTestParameterFactoryForm(engine, factory));
	}

	private void generateTestParameterFactoryForm(VelocityEngine engine, TestParameterFactoryForm metadata) {
		generateFile(engine, Map.of(ACTION, metadata, "rows", metadata.parameters()), "TestParameterFactoryForm.form.vm",
				metadata.formKey() + ".form");
	}

	private void generateActionForm(VelocityEngine engine, ActionForm metadata) {
		final var context = new VelocityContext(new HashMap<>(Map.of(ACTION, metadata)));
		for (int i = 0; i < metadata.parameters().size(); i++) {
			context.put("param" + i, metadata.parameters().get(i));
		}
		context.put("form", metadata);
		generateFile(engine, context, "ActionForm.form.vm", metadata.formKey() + ".form");
	}

	public void generateActionForm(VelocityEngine engine, TestRole role, TestAction action) {

		final var metadata = createActionFormMetadata(action, role);
		metadata.parameters().forEach(param -> generateTestApiParameterForms(engine, param));

		generateActionForm(engine, metadata);
	}

	@Override
	public void generateAction(VelocityEngine engine, TestRole testRole, TestAction testAction) {
		super.generateAction(engine, testRole, testAction);
		generateActionForm(engine, testRole, testAction);
	}

	public VelocityEngine generate() throws IOException {
		final var engine = super.generate();

		generateFile(engine, new VelocityContext(), "service-TCW-REST-API.service", "service-TCW-REST-API.service");
		generateFile(engine, new VelocityContext(), "template-TCW-BODY-JSON.tpl", "template-TCW-BODY-JSON.tpl");

		try (final var files = Files.list(outputFolder)) {
			final var appContext = new VelocityContext(new HashMap<>(Map.of(ACTION,
					new TestDictionaryMetadata("TCW-SIMPLE-EXAMPLE", "TCW-SIMPLE-EXAMPLE", "Simple example"),
					"childModels", files.map(f -> f.getFileName().toString().split("\\.")).map(split -> {
						if ("tpl".equals(split[1])) {
							return new AppModel(split[0].replace("template-", ""), "template");
						}
						return new AppModel(split[0], split[1]);
					}).toList())));
			generateFile(engine, appContext, "TCWM.app.vm", "TCWM.app");
		}

		try (var zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputFolder + ".zip")));
				final var files = Files.list(outputFolder)) {
			for (var file : files.toList()) {
				zip.putNextEntry(new ZipEntry(file.getFileName().toString()));
				zip.write(Files.readAllBytes(file));
			}
		}
		return engine;
	}

}