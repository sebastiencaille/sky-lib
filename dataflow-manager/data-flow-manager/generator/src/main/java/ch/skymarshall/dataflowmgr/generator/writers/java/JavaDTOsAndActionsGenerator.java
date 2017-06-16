/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.generator.writers.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import ch.skymarshall.dataflowmgr.generator.Utils;
import ch.skymarshall.dataflowmgr.generator.exceptions.GeneratorException;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.ModuleVisitor;

/**
 * Generates the dto and action classes
 * 
 * @author scaille
 *
 */
public abstract class JavaDTOsAndActionsGenerator extends ModuleVisitor<Map<String, String>> {

	protected final Map<String, String> registry = new HashMap<>();

	/**
	 * Generates the code that allows calling an action point
	 *
	 * @param context
	 * @param nextAP
	 * @return
	 */
	protected abstract String createActionCaller(final Map<String, String> context, final ActionPoint next);

	public JavaDTOsAndActionsGenerator(final Module module, final AbstractWriter abstractWriter) {
		super(module, abstractWriter);
	}

	/**
	 * Creates the dto file
	 */
	@Override
	public Map<String, String> visit(final Module module, final Dto dto, final Map<String, String> context) {
		final Map<String, String> scoped = createClassContext(context, module.packageName);
		scoped.put("dto.name", dto.name);

		super.visit(module, dto, scoped);
		final Template template = getTemplate(TEMPLATE.DTO, scoped);

		try {
			writeFile("/dto/" + Utils.firstUpperCase(dto.name) + ".java", template);
		} catch (final IOException e) {
			throw new GeneratorException("Unable to write file", e);
		}
		return context;
	}

	private Map<String, String> setFieldInfo(final Map<String, String> context, final String name, final String type) {
		final HashMap<String, String> scoped = new HashMap<>(context);
		scoped.put("field.name", name);
		scoped.put("field.nameCamel", Utils.firstUpperCase(name));
		scoped.put("field.type", type);
		return scoped;
	}

	@Override
	public Map<String, String> visitField(final Module module, final Dto dto, final Entry<String, String> field,
			final Map<String, String> context) {
		final Map<String, String> scoped = setFieldInfo(context, field.getKey(), field.getValue());
		final Template template = getTemplate(TEMPLATE.FIELD, scoped);
		return append(context, "fields", template.generate());
	}

	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final InFlowRule rule,
			final Map<String, String> context) {

		String transform;
		if (rule.transformFunction != null) {
			transform = "(flowIn) -> " + rule.transformFunction;
		} else {
			transform = "(flowIn, apIn) -> " + rule.transformConsumer;
		}

		final String code = String.format(
				"final InFlowDecisionRule<%s,%s> %s = %s.addInFlowRule(%s, %s, (flowIn) -> %s, %s);\n", rule.input,
				ap.input, variableName(ap, rule), variableName(ap), uuid(rule.uuid), rule.input + ".class",
				rule.activator, transform);
		append(context, "flow.inputRules", code);
		registry.put(variableName(ap, rule), variableName(ap, rule));
		return context;
	}

	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final OutFlowRule rule,
			final Map<String, String> context) {

		final ActionPoint nextAP = findAction(module, rule.nextAction);

		final String nextActionRef = createActionCaller(context, nextAP);
		String code = String.format(
				"final OutFlowDecisionRule<%s, %s> %s = OutFlowDecisionRule.output(%s, (apOut) -> %s, %s, %s, (apOut) ->  %s);\n",
				ap.output, rule.output, variableName(ap, rule), uuid(rule.uuid), rule.activator,
				"FlowActionType.CONTINUE", nextActionRef, rule.transformFunction);
		code += variableName(ap) + ".addOutputRule(" + variableName(ap, rule) + ");\n";
		append(context, "flow.outputRules", code);
		registry.put(variableName(ap, rule), variableName(ap, rule));
		return context;
	}

	/**
	 * Writes the action class
	 */
	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final Map<String, String> context) {
		final Map<String, String> scoped = createClassContext(context, module.packageName);
		super.visit(module, ap, context);
		scoped.put("action.name", ap.name);
		scoped.put("action.input", ap.input);

		final Set<String> imported = packages(module, ap.input, ap.output);

		final String body;
		final String output;
		if (ap.terminal) {
			body = ap.action.content + ";\n\t\t\treturn NO_DATA";
			output = "NoData";
			imported.addAll(packages(module, "NoData"));
		} else {
			body = "return " + ap.action.content;
			output = ap.output;
		}
		scoped.put("action.body", body);
		scoped.put("action.output", output);

		scoped.put("imports", toImports(imported));

		final Template template = getTemplate(TEMPLATE.ACTION, scoped);

		try {
			writeFile("/actions/" + className(ap) + ".java", template);
		} catch (final IOException e) {
			throw new GeneratorException("Unable to write file", e);
		}
		return context;
	}

	protected String uuid(final UUID uuid) {
		return "UUID.fromString(\"" + uuid + "\")";
	}

	/**
	 * Creates a context for a given generated class
	 *
	 * @param parentContext
	 * @param packageName
	 * @return
	 */
	protected Map<String, String> createClassContext(final Map<String, String> parentContext,
			final String packageName) {
		final HashMap<String, String> scoped = new HashMap<>(parentContext);
		scoped.put("package", packageName);
		return scoped;
	}

	/**
	 * Append value to the value of a context key
	 *
	 * @param context
	 * @param key
	 * @param valueJavaDTOVisitor
	 * @return
	 */
	protected Map<String, String> append(final Map<String, String> context, final String key, final String value) {
		final String current = context.get(key);
		if (current != null) {
			context.put(key, current + value);
		} else {
			context.put(key, value);
		}
		return context;
	}

	protected String className(final ActionPoint ap) {
		return Utils.firstUpperCase(ap.name);
	}

	protected String variableName(final ActionPoint ap) {
		return Utils.firstLowerCase(ap.name);
	}

	protected String variableName(final ActionPoint ap, final InFlowRule in) {
		return Utils.firstLowerCase(ap.name + "_in_" + in.uuid).replaceAll("-", "_");
	}

	protected String variableName(final ActionPoint ap, final OutFlowRule out) {
		return Utils.firstLowerCase(ap.name + "_out_" + out.uuid).replaceAll("-", "_");
	}

	protected Set<String> packages(final Module module, final String... classes) {
		final Set<String> toImport = new HashSet<>();
		for (final String clazz : classes) {
			addImportOfClass(module, toImport, clazz);
		}
		return toImport;
	}

	protected String toImports(final Set<String> toImport) {
		final StringBuilder imports = new StringBuilder();
		for (final String imp : toImport) {
			imports.append("import ").append(imp).append(";\n");
		}
		return imports.toString();
	}

	/**
	 * Add the import of clazz to toImport
	 *
	 * @param module
	 * @param toImport
	 * @param clazz
	 */
	protected void addImportOfClass(final Module module, final Set<String> toImport, final String clazz) {
		if (clazz == null) {
			return;
		}
		try {
			Class.forName(clazz);
			return;
		} catch (final ClassNotFoundException e) {
		}
		for (final Dto dto : module.dtos) {
			if (dto.name.equals(clazz)) {
				toImport.add(module.packageName + ".dto." + clazz);
				return;
			}
		}
		for (final ActionPoint ap : module.actions) {
			if (ap.name.equals(clazz)) {
				toImport.add(module.packageName + ".actions." + clazz);
				return;
			}
		}
		if (tryImport(toImport, "ch.skymarshall.dataflowmgr.model." + clazz)) {
			return;
		}
		if (tryImport(toImport, "java.lang." + clazz)) {
			return;
		}

	}

	private boolean tryImport(final Set<String> toImport, final String clazz) {
		try {
			Class.forName(clazz);
			toImport.add(clazz);
			return true;
		} catch (final ClassNotFoundException e2) {
			// fallback
			return false;
		}
	}

}