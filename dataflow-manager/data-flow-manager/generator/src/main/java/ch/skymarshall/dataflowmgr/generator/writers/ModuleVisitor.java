package ch.skymarshall.dataflowmgr.generator.writers;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Dto;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;

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
		final Template newTemplate = writer.registry.getTemplate(template);
		newTemplate.setContext(context);
		return newTemplate;
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

	public T visitField(final Module module2, final Dto dto, final Entry<String, String> field, final T context) {
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
			result = visitField(module, ap, rule, context);
		}
		return result;
	}

	public T visitField(final Module module, final ActionPoint ap, final OutFlowRule rule, final T context) {
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

}
