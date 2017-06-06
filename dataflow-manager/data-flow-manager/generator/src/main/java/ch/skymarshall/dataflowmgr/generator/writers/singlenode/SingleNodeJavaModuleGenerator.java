package ch.skymarshall.dataflowmgr.generator.writers.singlenode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.Utils;
import ch.skymarshall.dataflowmgr.generator.exceptions.GeneratorException;
import ch.skymarshall.dataflowmgr.generator.model.ActionPoint;
import ch.skymarshall.dataflowmgr.generator.model.Flow;
import ch.skymarshall.dataflowmgr.generator.model.InFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Module;
import ch.skymarshall.dataflowmgr.generator.model.OutFlowRule;
import ch.skymarshall.dataflowmgr.generator.model.Template;
import ch.skymarshall.dataflowmgr.generator.model.Template.TEMPLATE;
import ch.skymarshall.dataflowmgr.generator.writers.AbstractWriter;
import ch.skymarshall.dataflowmgr.generator.writers.java.JavaDTOsAndActionsGenerator;

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
	protected String createActionCaller(final Map<String, String> context, final ActionPoint ap) {
		return "LocalAPRef.local(" + variableName(ap) + ")";
	}

	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final InFlowRule rule,
			final Map<String, String> context) {
		flowImportPackages.addAll(packages(module, rule.input, ap.input));
		return super.visit(module, ap, rule, context);
	}

	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final OutFlowRule rule,
			final Map<String, String> context) {
		flowImportPackages.add("ch.skymarshall.dataflowmgr.model.LocalAPRef");
		flowImportPackages.addAll(packages(module, rule.output, ap.output));
		return super.visit(module, ap, rule, context);
	}

	@Override
	public Map<String, String> visit(final Module module, final ActionPoint ap, final Map<String, String> context) {
		super.visit(module, ap, context);

		final Set<String> imported = packages(module, ap.input, ap.output);
		if (ap.terminal) {
			imported.addAll(packages(module, "NoData"));
		}
		flowImportPackages.addAll(imported);
		flowImportPackages.addAll(packages(module, ap.name));

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
		return context;
	}

	@Override
	public Map<String, String> visit(final Module module, final Flow flow, final Map<String, String> context) {
		registry.clear();
		final Map<String, String> scoped = createClassContext(context, module.packageName);

		super.visit(module, flow, scoped);
		scoped.put("flow.name", flow.name);
		scoped.put("flow.uuid", uuid(flow.uuid));
		scoped.put("flow.input", flow.input);
		scoped.put("flow.firstAction", variableName(findAction(module, flow.action)));

		// Imports
		final Set<String> depPackages = packages(module, flow.input);
		depPackages.addAll(flowImportPackages);
		scoped.put("imports", toImports(depPackages));

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

}
