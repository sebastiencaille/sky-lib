package ch.skymarshall.dataflowmgr.generator.writers.java;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.BindingRule;
import ch.skymarshall.dataflowmgr.model.Call;
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.JavaCodeGenerator;
import ch.skymarshall.util.generators.Template;

public class FlowToProceduralJavaVisitor extends AbstractJavaVisitor {

	private final JavaCodeGenerator generator = new JavaCodeGenerator();

	public FlowToProceduralJavaVisitor(final Flow flow, final String packageName, final Template template) {
		super(flow, packageName, template);
	}

	public Template process() throws IOException {

		availableVars.add(new BindingImplVariable(Flow.ENTRY_POINT, flow.getEntryPointType(), Flow.ENTRY_POINT));

		super.processFlow();

		final Map<String, String> templateProperties = new HashMap<>();
		templateProperties.put("package", packageName);
		templateProperties.put("flow.name", flow.getName());
		templateProperties.put("flow.input", flow.getEntryPointType());
		templateProperties.put("flow.output", "void");
		templateProperties.put("flow.code", generator.toString());
		templateProperties.put("imports", imports.stream().map(i -> "import " + i + ";").collect(joining("\n")));
		return template.apply(templateProperties, JavaCodeGenerator.classToSource(packageName, flow.getName()));
	}

	@Override
	protected void process(final BindingContext context, final Processor processor) throws IOException {

		appendInfo(generator, context.binding).eol();

		availableVars.add(
				new BindingImplVariable(context.outputDataPoint, processor.getReturnType(), context.outputDataPoint));

		// Init all data
		// ----------------
		final boolean isConditionalInputDataPoint = isConditional(context.inputDataPoint);

		final Set<String> exclusions = BindingRule
				.getAll(context.binding.getRules(), BindingRule.Type.EXCLUSION, Binding.class).map(Binding::toDataPoint)
				.collect(toSet());
		final boolean isConditionalExec = isConditionalInputDataPoint || !context.activators.isEmpty()
				|| !exclusions.isEmpty();
		final boolean isExit = Flow.EXIT_PROCESSOR.equals(context.outputDataPoint);

		visitActivators(context);
		if (!isExit && !definedDataPoints.contains(context.outputDataPoint)) {
			generateDataPoint(processor, context.outputDataPoint, isConditionalExec);
		}

		// Conditional Execution
		// -------------------------
		if (!exclusions.isEmpty()) {
			generator.addLocalVariable("boolean", executeDefaultVarNameOf(context),
					exclusions.stream().map(x -> "!" + availableVarNameOf(x)).collect(joining(" && "))).eol();
		}
		addExecution(context, processor, exclusions, isConditionalInputDataPoint, isConditionalExec, isExit);
		generator.eol();
	}

	private void addExecution(final BindingContext context, final Processor processor, final Set<String> exclusions,
			final boolean conditionalInputDataPoint, final boolean isConditionalExec, final boolean isExit)
			throws IOException {
		if (isConditionalExec) {
			setConditional(context.outputDataPoint);
			final List<String> conditions = new ArrayList<>();
			if (conditionalInputDataPoint) {
				conditions.add(availableVarNameOf(context.inputDataPoint));
			}
			if (!context.activators.isEmpty()) {
				conditions.add(activatedVarNameOf(context.binding));
			}
			if (!exclusions.isEmpty()) {
				conditions.add(executeDefaultVarNameOf(context));
			}
			generator.openIf(String.join(" && ", conditions));
		}

		// Execution
		visitExternalAdapters(context, context.unprocessedAdapters(context.adapters));
		if (!isExit) {
			generator.appendIndent();
			if (isConditionalExec) {
				generator.append(context.outputDataPoint).append(" = ");
			}
			appendCall(context, processor);
			if (isConditionalExec) {
				generator.appendIndented(availableVarNameOf(context.outputDataPoint)).append(" = true").eos();
			}
		}
		if (isConditionalExec) {
			generator.closeBlock();
		}
	}

	private void generateDataPoint(final Processor processor, final String outputDataPoint,
			final boolean conditionalExec) throws IOException {
		definedDataPoints.add(outputDataPoint);
		appendNewVariable(outputDataPoint, processor);
		if (conditionalExec) {
			generator.append(" = null").eos();
			generator.addLocalVariable("boolean", availableVarNameOf(outputDataPoint), "false");
		} else {
			generator.append(" = ");
		}
	}

	/**
	 * Generates the code calling a list of activators
	 *
	 * @param context
	 * @param availableVars
	 * @throws IOException
	 */
	private void visitActivators(final BindingContext context) throws IOException {
		if (context.activators.isEmpty()) {
			return;
		}
		generator.addLocalVariable("boolean", activatedVarNameOf(context.binding), "true");
		for (final Condition activator : context.activators) {
			generator.openIf(activatedVarNameOf(context.binding));

			final Set<ExternalAdapter> unprocessed = context.unprocessedAdapters(listAdapters(context, activator));
			visitExternalAdapters(context, unprocessed);
			context.processedAdapters.addAll(unprocessed);

			generator.appendIndented(activatedVarNameOf(context.binding)).append(" &= ");
			appendCall(context, activator);

			generator.closeBlock();
		}
	}

	private void visitExternalAdapters(final BindingContext context, final Set<ExternalAdapter> externalAdapter)
			throws IOException {
		for (final ExternalAdapter adapter : externalAdapter) {
			final BindingImplVariable parameter = new BindingImplVariable(adapter, varNameOf(context.binding, adapter));
			appendNewVarAndCall(context, parameter.variable, adapter);
			availableVars.add(parameter);
		}
	}

	/** Specifies if the default binding must be executed */
	private String executeDefaultVarNameOf(final BindingContext context) {
		return context.outputDataPoint + "_executeDefault";
	}

	/**
	 * Specifies if a binding is activated
	 *
	 * @param binding
	 * @return
	 */
	private String activatedVarNameOf(final Binding binding) {
		return "activated_" + toVariable(binding);
	}

	private void appendNewVariable(final String variableName, final Call<?> call) throws IOException {
		String returnType = call.getReturnType();
		if (returnType.startsWith("java.lang")) {
			returnType = returnType.substring("java.lang".length() + 1);
		}
		generator.appendIndented(returnType).append(" ").append(variableName);
	}

	private void appendCall(final BindingContext context, final Call<?> call) throws IOException {
		generator.appendMethodCall("this", call.getCall(), guessParameters(context, call)).eos();
	}

	private void appendNewVarAndCall(final BindingContext context, final String variableName, final Call<?> call)
			throws IOException {
		if (call.hasReturnType()) {
			appendNewVariable(variableName, call);
			generator.append(" = ");
		} else {
			generator.appendIndent();
		}
		appendCall(context, call);
	}

}
