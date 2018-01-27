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
package ch.skymarshall.dataflowmgr.generator.writers.singlenode;

import static org.skymarshall.util.generators.JavaCodeGenerator.toImports;
import static org.skymarshall.util.generators.Template.append;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.skymarshall.util.generators.Template;

import ch.skymarshall.dataflowmgr.generator.Utils;
import ch.skymarshall.dataflowmgr.generator.exceptions.GeneratorException;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.java.JavaDTOsAndActionsGenerator;
import ch.skymarshall.dataflowmgr.local.LocalAPRef;

/**
 * Writes the flow factory (+DTOs and actions) that can be used with the single
 * node engine
 *
 * @author scaille
 *
 */
public class SingleNodeJavaModuleGenerator extends JavaDTOsAndActionsGenerator {

	private final Set<String> flowImportPackages = new HashSet<>();

	public SingleNodeJavaModuleGenerator(final Module module, final AbstractWriter writer) {
		super(module, writer);
	}

	@Override
	protected String createActionCaller(final Map<String, String> context, final String flowType,
			final String[] nextActions) {

		final Set<String> actionPointRef = forEachActionPoint(flowType, nextActions, this::variableName);
		final Set<String> inputFlowRef = forEachInputFlow(flowType, nextActions, this::variableName);

		String result = "LocalAPRef.refTo(" + String.join(",", actionPointRef) + ")";
		if (!inputFlowRef.isEmpty()) {
			result += ".addAll(LocalAPRef.refToApOf(" + String.join(",", inputFlowRef) + "))";
		}
		return result;
	}

	@Override
	public Map<String, String> visit(final ActionPoint ap, final InFlowRule rule, final Map<String, String> context) {
		flowImportPackages.addAll(packages(rule.input, ap.input));
		return super.visit(ap, rule, context);
	}

	@Override
	public Map<String, String> visit(final ActionPoint ap, final OutFlowRule rule, final Map<String, String> context) {
		flowImportPackages.addAll(packages(rule.output, ap.output));
		return super.visit(ap, rule, context);
	}

	@Override
	public Map<String, String> visit(final ActionPoint ap, final Map<String, String> context) {
		super.visit(ap, context);

		final Set<String> imported = packages(ap.input, ap.output);
		flowImportPackages.addAll(imported);
		flowImportPackages.addAll(packages(ap.name));

		final String code;
		if (ap.terminal) {
			code = String.format("final ActionPoint<%s, ?> %s = ActionPoint.terminal(%s, new %s());\n", ap.input,
					variableName(ap), uuid(ap.uuid), className(ap)); // NOSONAR
		} else {
			code = String.format("final ActionPoint<%s, %s> %s = ActionPoint.simple(%s, new %s());\n", ap.input,
					ap.output, variableName(ap), uuid(ap.uuid), className(ap)); // NOSONAR
		}
		append(context, "flow.actions", code);

		registry.put(variableName(ap), ap.name);
		return context;
	}

	@Override
	public Map<String, String> visit(final Flow flow, final Map<String, String> context) {
		registry.clear();
		flowImportPackages.clear();

		context.put("flow.init", "");

		final Map<String, String> scoped = createClassContext(context, module.packageName);

		super.visit(flow, scoped);

		addImportOfClass(flowImportPackages, LocalAPRef.class.getName());

		scoped.put("flow.name", flow.name);
		scoped.put("flow.uuid", uuid(flow.uuid));
		scoped.put("flow.input", flow.input);
		scoped.put("flow.firstAction", variableName(findActionPoints(flow.action).get(0)));

		// Imports
		final Set<String> depPackages = packages(flow.input);
		depPackages.addAll(flowImportPackages);
		scoped.put("imports", toImports(depPackages));

		// Registry
		final StringBuilder regs = new StringBuilder();
		for (final Entry<String, String> reg : registry.entrySet()) {
			regs.append(String.format("registry.registerObject(%s, \"%s\");\n", reg.getKey(), reg.getValue())); // NOSONAR
		}
		scoped.put("flow.registry", regs.toString());
		final Template template = getTemplate(TEMPLATE.FLOW, scoped);

		try {
			writeFile("/" + Utils.firstUpperCase(flow.name) + "Factory.java", template);
		} catch (final IOException e) {
			throw new GeneratorException("Unable to write file", e);
		}
		return context;
	}

}
