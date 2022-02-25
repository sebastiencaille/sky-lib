package ch.scaille.dataflowmgr.generator.writers.javaproc;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.scaille.dataflowmgr.model.Binding;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.JavaCodeGenerator;

public class ConditionalFlowCtrlGenerator extends AbstractFlowGenerator {

	public ConditionalFlowCtrlGenerator(FlowToProceduralJavaVisitor visitor,
			JavaCodeGenerator<RuntimeException> generator) {
		super(visitor, generator);
	}

	@Override
	public boolean matches(BindingContext context) {
		return ConditionalFlowCtrl.getCondition(context.binding.getRules()).isPresent();
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
		return "activated_" + visitor.toVariable(binding);
	}

	@Override
	public void generate(BaseGenContext<Void> genContext, BindingContext context) {
		visitor.setConditional(context.outputDataPoint);

		visitActivators(context);

		var activators = ConditionalFlowCtrl.getActivators(context.binding.getRules()).collect(toList());
		var exclusions = ConditionalFlowCtrl.getExclusions(context.binding.getRules()).map(Binding::toDataPoint)
				.collect(toSet());

		if (!exclusions.isEmpty()) {
			generator
					.addLocalVariable(Boolean.TYPE.getName(), executeDefaultVarNameOf(context),
							exclusions.stream().map(x -> "!" + visitor.availableVarNameOf(x)).collect(joining(" && ")))
					.eol();
		}

		generateDataPoint(context);
		final var conditions = new ArrayList<String>();
		if (visitor.isConditionalData(context.inputDataPoint)) {
			conditions.add(visitor.availableVarNameOf(context.inputDataPoint));
		}
		if (!activators.isEmpty()) {
			conditions.add(activatedVarNameOf(context.binding));
		}
		if (!exclusions.isEmpty()) {
			conditions.add(executeDefaultVarNameOf(context));
		}
		generator.openIf(String.join(" && ", conditions));
		genContext.next(context);
		generator.appendIndented(visitor.availableVarNameOf(context.outputDataPoint)).append(" = true").eos();
		generator.closeBlock();

	}

	private void generateDataPoint(BindingContext context) {
		if (visitor.definedDataPoints.contains(context.outputDataPoint)) {
			return;
		}
		visitor.definedDataPoints.add(context.outputDataPoint);
		visitor.appendNewVariable(context.outputDataPoint, context.getProcessor());
		generator.append(" = null").eos();
		generator.addLocalVariable(Boolean.TYPE.getName(), visitor.availableVarNameOf(context.outputDataPoint),
				"false");
	}

	/**
	 * Generates the code calling a list of activators
	 *
	 * @param context
	 * @param availableVars @
	 */
	private void visitActivators(final BindingContext context) {
		var activators = ConditionalFlowCtrl.getActivators(context.binding.getRules()).collect(toList());
		if (activators.isEmpty()) {
			return;
		}
		generator.addLocalVariable(Boolean.TYPE.getName(), activatedVarNameOf(context.binding), "true");
		for (final var activator : activators) {
			generator.openIf(activatedVarNameOf(context.binding));

			final var unprocessed = context.unprocessedAdapters(visitor.listAdapters(context, activator));
			visitor.visitExternalAdapters(context, unprocessed);
			context.processedAdapters.addAll(unprocessed);

			generator.appendIndented(activatedVarNameOf(context.binding)).append(" &= ");
			visitor.appendCall(context, activator);

			generator.closeBlock();
		}
	}
}
