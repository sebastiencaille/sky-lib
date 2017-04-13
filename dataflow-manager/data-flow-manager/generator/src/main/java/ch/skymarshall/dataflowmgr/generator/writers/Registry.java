package ch.skymarshall.dataflowmgr.generator.writers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.model.Transformer;

public class Registry {

	private final Map<String, Transformer> transformers = new HashMap<>();
	private final Map<String, Dto> dtos = new HashMap<>();
	private final Map<String, ActionPoint> actions = new HashMap<>();
	private final Map<String, Flow> flows = new HashMap<>();
	private final Map<TEMPLATE, Template> templates = new HashMap<>();

	public Registry() {
	}

	public void addTransformer(final Transformer template) {
		transformers.put(template.name, template);
	}

	public Transformer getTransformer(final String name) {
		return transformers.get(name);
	}

	public void addDto(final Dto dto) {
		dtos.put(dto.name, dto);
	}

	public void addActionPoint(final ActionPoint action) {
		actions.put(action.name, action);
	}

	public void addFlow(final Flow flow) {
		flows.put(flow.name, flow);
	}

	public Collection<ActionPoint> getActions() {
		return actions.values();
	}

	public void addTemplate(final TEMPLATE templateName, final Template content) {
		templates.put(templateName, content);
	}

	public Template getTemplate(final TEMPLATE templateName) {
		return templates.get(templateName).clone();
	}

}
