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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.skymarshall.util.generators.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.TEMPLATE;

public class ModuleVisitor<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleVisitor.class);

	private final AbstractWriter writer;
	private final Module module;

	public ModuleVisitor(final Module module, final AbstractWriter abstractWriter) {
		this.module = module;
		this.writer = abstractWriter;
		LOGGER.info("Module location: " + writer.getModuleLocation(module));
	}

	protected Template getTemplate(final TEMPLATE template, final Map<String, String> context) {
		return writer.registry.getTemplate(template, context);
	}

	protected void writeFile(final String filename, final Template template) throws IOException {
		final File moduleLocation = writer.getModuleLocation(module);
		template.write(new File(moduleLocation, module.packageName.replaceAll(Pattern.quote("."), "/") + filename));
	}

	public T visit(final T context) {
		T result = context;
		for (final Flow flow : module.flows) {
			result = visit(module, flow, context);
		}
		return result;
	}

	public T visitField(final Module module, final Dto dto, final Entry<String, String> field, final T context) {
		return context;
	}

	public T visit(final Module module, final Dto dto, final T context) {
		T result = context;
		for (final Map.Entry<String, String> field : dto.fields.entrySet()) {
			result = visitField(module, dto, field, context);
		}
		return result;
	}

	public T visit(final Module module, final ActionPoint ap, final T context) {
		T result = context;
		for (final InFlowRule rule : ap.inputRules) {
			result = visit(module, ap, rule, context);
		}
		for (final OutFlowRule rule : ap.outputRules) {
			result = visit(module, ap, rule, context);
		}
		return result;
	}

	public T visit(final Module module, final ActionPoint ap, final OutFlowRule rule, final T context) {
		return context;
	}

	public T visit(final Module module, final ActionPoint ap, final InFlowRule rule, final T context) {
		return context;
	}

	public T visit(final Module module, final Flow flow, final T context) {
		T result = context;
		for (final Dto dto : module.dtos) {
			result = visit(module, dto, context);
		}
		for (final ActionPoint action : module.actions) {
			result = visit(module, action, context);
		}

		return result;
	}

	protected ActionPoint findAction(final Module module, final String actionName) {
		return module.actions.stream().filter(action -> action.name.equals(actionName)).findFirst()
				.orElseThrow(() -> new IllegalStateException("Unable to find action " + actionName));
	}

}
