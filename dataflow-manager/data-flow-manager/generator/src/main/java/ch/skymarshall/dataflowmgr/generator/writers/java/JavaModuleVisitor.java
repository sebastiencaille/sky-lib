package ch.skymarshall.dataflowmgr.generator.writers.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.Utils;
import ch.skymarshall.dataflowmgr.generator.exceptions.GeneratorException;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.ModuleVisitor;

public class JavaModuleVisitor extends ModuleVisitor<Map<String, String>> {

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

	private Map<String, String> setClassInfo(final Map<String, String> context, final String packageName,
			final String className) {
		final HashMap<String, String> scoped = new HashMap<>(context);
		scoped.put("package", packageName);
		scoped.put("dto.name", className);
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
		final Map<String, String> scoped = setClassInfo(context, module.packageName, dto.name);
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
	public Map<String, String> visit(final Module module, final ActionPoint ap, final Map<String, String> context) {
		final Map<String, String> scoped = setClassInfo(context, module.packageName, ap.name);
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
			writeFile("/actions/" + Utils.firstUpperCase(ap.name) + ".java", template);
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
