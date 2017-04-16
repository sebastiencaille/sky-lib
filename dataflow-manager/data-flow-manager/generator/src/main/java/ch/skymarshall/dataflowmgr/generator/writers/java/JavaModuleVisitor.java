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
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.ModuleVisitor;

public class JavaModuleVisitor extends ModuleVisitor<Map<String, String>> {

	private final Map<String, String> registry = new HashMap<>();
	private final Set<String> flowPackages = new HashSet<>();

	public JavaModuleVisitor(final Module module, final AbstractWriter writer) {
		super(module, writer);
	}

	private Map<String, String> append(final Map<String, String> context, final String key, final String value) {
		final String current = context.get(key);
		if (current != null) {
			context.put(key, current + value);
		} else {
			context.put(key, value);
		}
		return context;
	}

	private Map<String, String> setClassInfo(final Map<String, String> context, final String packageName) {
		final HashMap<String, String> scoped = new HashMap<>(context);
		scoped.put("package", packageName);
		return scoped;
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
	public Map<String, String> visit(final Module module, final Dto dto, final Map<String, String> context) {
		final Map<String, String> scoped = setClassInfo(context, module.packageName);
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
		flowPackages.addAll(packages(module, rule.input, ap.input));
		return context;
	}

	@Override
	public Map<String, String> visitField(final Module module, final ActionPoint ap, final OutFlowRule rule,
			final Map<String, String> context) {

		final ActionPoint nextAP = findAction(module, rule.nextAction);

		final String nextActionRef = createActionRef(context, nextAP);
		String code = String.format(
				"final OutFlowDecisionRule<%s, %s> %s = OutFlowDecisionRule.output(%s, (apOut) -> %s, %s, %s, (apOut) ->  %s);\n",
				ap.output, rule.output, variableName(ap, rule), uuid(rule.uuid), rule.activator,
				"FlowActionType.CONTINUE", nextActionRef, rule.transformFunction);
		code += variableName(ap) + ".addOutputRule(" + variableName(ap, rule) + ");\n";
		append(context, "flow.outputRules", code);
		registry.put(variableName(ap, rule), variableName(ap, rule));
		flowPackages.addAll(packages(module, rule.output, ap.output));
		return context;
	}

	protected String createActionRef(final Map<String, String> context, final ActionPoint ap) {
		flowPackages.add("ch.skymarshall.dataflowmgr.model.LocalAPRef");
		return "LocalAPRef.local(" + variableName(ap) + ")";
	}

	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final Map<String, String> context) {
		final Map<String, String> scoped = setClassInfo(context, module.packageName);
		super.visit(module, ap, context);
		scoped.put("action.name", ap.name);
		scoped.put("action.input", ap.input);

		final Set<String> imported = packages(module, ap.input, ap.output);

		final String body;
		final String output;
		if (ap.terminal) {
			body = ap.action.content + "(input);\n\t\t\treturn NO_DATA";
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

		final String code;
		if (ap.terminal) {
			code = String.format("ActionPoint<%s, ?> %s = ActionPoint.terminal(%s, new %s());\n", ap.input,
					variableName(ap), uuid(ap.uuid), className(ap));
		} else {
			code = String.format("ActionPoint<%s, %s> %s = ActionPoint.simple(%s, new %s());\n", ap.input, ap.output,
					variableName(ap), uuid(ap.uuid), className(ap));
		}
		append(context, "flow.actions", code);

		registry.put(variableName(ap), ap.name);
		flowPackages.addAll(imported);
		flowPackages.addAll(packages(module, ap.name));
		return context;
	}

	private String className(final ActionPoint ap) {
		return Utils.firstUpperCase(ap.name);
	}

	@Override
	public Map<String, String> visit(final Module module, final Flow flow, final Map<String, String> context) {
		registry.clear();
		final Map<String, String> scoped = setClassInfo(context, module.packageName);

		super.visit(module, flow, scoped);
		scoped.put("flow.name", flow.name);
		scoped.put("flow.uuid", uuid(flow.uuid));
		scoped.put("flow.input", flow.input);
		scoped.put("flow.firstAction", variableName(findAction(module, flow.action)));

		// Imports
		final Set<String> packages = packages(module, flow.input);
		packages.addAll(flowPackages);
		scoped.put("imports", toImports(packages));

		// Registry
		final StringBuilder regs = new StringBuilder();
		for (final Entry<String, String> reg : registry.entrySet()) {
			regs.append(String.format("registry.registerObject(%s, \"%s\");\n", reg.getKey(), reg.getValue()));
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

	private Set<String> packages(final Module module, final String... classes) {
		final Set<String> toImport = new HashSet<>();
		for (final String clazz : classes) {
			resolveClass(module, toImport, clazz);

		}
		return toImport;
	}

	private String toImports(final Set<String> toImport) {
		final StringBuilder imports = new StringBuilder();
		for (final String imp : toImport) {
			imports.append("import ").append(imp).append(";\n");
		}
		return imports.toString();
	}

	private void resolveClass(final Module module, final Set<String> toImport, final String clazz) {
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

	private ActionPoint findAction(final Module module, final String actionName) {
		return module.actions.stream().filter((action) -> {
			return action.name.equals(actionName);
		}).findFirst().orElseThrow(() -> new IllegalStateException("Unable to find action " + actionName));
	}

	private String variableName(final ActionPoint ap) {
		return Utils.firstLowerCase(ap.name);
	}

	private String variableName(final ActionPoint ap, final InFlowRule in) {
		return Utils.firstLowerCase(ap.name + "_in_" + in.uuid).replaceAll("-", "_");
	}

	private String variableName(final ActionPoint ap, final OutFlowRule out) {
		return Utils.firstLowerCase(ap.name + "_out_" + out.uuid).replaceAll("-", "_");
	}

	private String uuid(final UUID uuid) {
		return "UUID.fromString(\"" + uuid + "\")";
	}
}
