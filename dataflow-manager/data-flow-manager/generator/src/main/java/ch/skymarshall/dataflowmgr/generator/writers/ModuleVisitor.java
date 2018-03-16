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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	protected final Module module;

	protected Map<String, List<ActionPoint>> broadcastGroups = new HashMap<>();

	public ModuleVisitor(final Module module, final AbstractWriter abstractWriter) {
		this.module = module;
		this.writer = abstractWriter;
		LOGGER.info("Module location: " + writer.getModuleLocation(module));

		for (final ActionPoint action : module.actions) {
			addToBroadcastGroups(action.broadcastGroups, action);
			addToBroadcastGroups(new String[] { action.name, action.input }, action);
			action.inputRules.forEach(rule -> addToBroadcastGroup(rule.input, action));
		}

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
			result = visit(flow, context);
		}
		return result;
	}

	public T visitField(final Dto dto, final Entry<String, String> field, final T context) {
		return context;
	}

	public T visit(final Dto dto, final T context) {
		T result = context;
		for (final Map.Entry<String, String> field : dto.fields.entrySet()) {
			result = visitField(dto, field, context);
		}
		return result;
	}

	public T visit(final ActionPoint ap, final T context) {

		T result = context;
		for (final InFlowRule rule : ap.inputRules) {
			result = visit(ap, rule, context);
		}
		for (final OutFlowRule rule : ap.outputRules) {
			result = visit(ap, rule, context);
		}
		return result;
	}

	public T visit(final ActionPoint ap, final OutFlowRule rule, final T context) {
		return context;
	}

	public T visit(final ActionPoint ap, final InFlowRule rule, final T context) {
		return context;
	}

	public T visit(final Flow flow, final T context) {
		T result = context;

		for (final Dto dto : module.dtos) {
			result = visit(dto, context);
		}

		for (final ActionPoint action : module.actions) {
			result = visit(action, context);
		}

		return result;
	}

	protected List<ActionPoint> findActionPoints(final String broadcastName) {
		final List<ActionPoint> list = broadcastGroups.get(broadcastName);
		if (list == null) {
			throw new IllegalStateException("No action point for " + broadcastName);
		}
		return list;
	}

	protected <U> Set<U> forEachInputFlow(final String flowType, final String[] nextActions,
			final BiFunction<ActionPoint, InFlowRule, U> function) {
		final Set<U> variables = new HashSet<>();
		for (final String action : nextActions) {
			String broadcastGroup = action;
			if ("broadcast-type".equals(broadcastGroup)) {
				broadcastGroup = flowType;
			}
			final List<ActionPoint> aps = findActionPoints(broadcastGroup);
			for (final ActionPoint ap : aps) {
				variables.addAll(ap.inputRules.stream().map(r -> function.apply(ap, r)).collect(Collectors.toList()));
			}
		}
		return variables;
	}

	protected <U> Set<U> forEachActionPoint(final String flowType, final String[] nextActions,
			final Function<ActionPoint, U> function) {
		final Set<U> variables = new HashSet<>();
		for (final String action : nextActions) {
			String broadcastGroup = action;
			if ("broadcast-type".equals(broadcastGroup)) {
				broadcastGroup = flowType;
			}
			final List<ActionPoint> aps = findActionPoints(broadcastGroup);
			variables.addAll(
					aps.stream().filter(ap -> ap.inputRules.isEmpty()).map(function).collect(Collectors.toList()));
		}
		return variables;
	}

	protected List<InFlowRule> findInputFlows(final ActionPoint ap, final String flowType) {
		return ap.inputRules.stream().filter(rule -> rule.input.equals(flowType)).collect(Collectors.toList());
	}

	protected void addToBroadcastGroups(final String[] groups, final ActionPoint ap) {
		if (groups == null) {
			return;
		}
		Arrays.stream(groups).forEach(group -> addToBroadcastGroup(group, ap));
	}

	private void addToBroadcastGroup(final String group, final ActionPoint ap) {
		broadcastGroups.computeIfAbsent(group, k -> new ArrayList<>()).add(ap);
	}

}
