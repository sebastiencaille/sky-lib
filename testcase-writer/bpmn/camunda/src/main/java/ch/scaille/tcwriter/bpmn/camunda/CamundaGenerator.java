package ch.scaille.tcwriter.bpmn.camunda;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import ch.scaille.tcwriter.bpmn.core.BpmnGenerator;
import ch.scaille.tcwriter.model.dictionary.TestAction;
import ch.scaille.tcwriter.model.dictionary.TestDictionary;
import ch.scaille.tcwriter.model.dictionary.TestRole;

public class CamundaGenerator extends BpmnGenerator {

	public record TestParameterFactoryParameterForm(String text, boolean mandatory) {
	}

	public CamundaGenerator(TestDictionary dictionary, Path outputFolder) {
		super("camunda", dictionary, outputFolder);
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
		generateActionForm(engine, metadata);
	}

	@Override
	public void generateAction(VelocityEngine engine, TestRole testRole, TestAction testAction) {
		super.generateAction(engine, testRole, testAction);
		generateActionForm(engine, testRole, testAction);
	}

	@Override
	public VelocityEngine generate() throws IOException {
	
		final var engine = super.generate();
		generateFile(engine, new VelocityContext(), "camunda.json", "camunda.json");
		return engine;
	}
	
}