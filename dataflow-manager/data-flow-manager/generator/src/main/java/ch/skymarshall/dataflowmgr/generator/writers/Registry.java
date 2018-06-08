/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.generator.writers;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.skymarshall.util.generators.Template;

import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.TemplateType;
import ch.skymarshall.dataflowmgr.generator.model.Transformer;

public class Registry {

	private final Map<String, Transformer> transformers = new HashMap<>();
	private final Map<String, Dto> dtos = new HashMap<>();
	private final Map<String, ActionPoint> actions = new HashMap<>();
	private final Map<String, Flow> flows = new HashMap<>();
	private final Map<TemplateType, Template> templates = new EnumMap<>(TemplateType.class);

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

	public void addTemplate(final TemplateType templateName, final Template content) {
		templates.put(templateName, content);
	}

	public Template getTemplate(final TemplateType templateName, final Map<String, String> context) {
		return templates.get(templateName).apply(context);
	}

}
