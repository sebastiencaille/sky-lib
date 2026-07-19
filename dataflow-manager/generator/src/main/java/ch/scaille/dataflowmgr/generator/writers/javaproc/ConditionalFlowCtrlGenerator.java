package ch.scaille.dataflowmgr.generator.writers.javaproc;

import static ch.scaille.dataflowmgr.generator.writers.AbstractJavaFlowVisitor.toVariable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.CallContext;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.JavaCodeGenerator;

public class ConditionalFlowCtrlGenerator extends AbstractFlowGenerator {

	public ConditionalFlowCtrlGenerator(FlowToProceduralJavaVisitor visitor,
			JavaCodeGenerator<RuntimeException> generator) {
		super(visitor, generator);
	}

	@Override
	public boolean matches(CallContext context) {
		return ConditionalFlowCtrl.getCondition(context.processor.getRules()).isPresent();
	}

	/** Specifies if the default call must be executed */
	private String executeDefaultVarNameOf(final CallContext context) {
		return context.outputDataPoint + "_executeDefault";
	}

	/**
	 * Specifies if a call is activated
	 *
     */
	private String activatedVarNameOf(final Processor processorCall) {
		return "activated_" + toVariable(processorCall);
	}

	@Override
	public void generate(BaseGenContext<Void> genContext, CallContext context) {
		visitor.setConditional(context.outputDataPoint);

		visitActivators(context);

		final var activators = ConditionalFlowCtrl.getActivators(context.processor.getRules()).toList();
		final var exclusions = ConditionalFlowCtrl.getExclusions(context.processor.getRules(), Processor.class).map(Processor::toDataPoint)
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
			conditions.add(activatedVarNameOf(context.processor));
		}
		if (!exclusions.isEmpty()) {
			conditions.add(executeDefaultVarNameOf(context));
		}
		generator.inIf(String.join(" && ", conditions), gen -> {
			genContext.run(context);
			gen.appendIndented(visitor.availableVarNameOf(context.outputDataPoint)).append(" = true").eos();
		});

	}

	private void generateDataPoint(CallContext context) {
		if (visitor.definedDataPoints.contains(context.outputDataPoint)) {
			return;
		}
		visitor.definedDataPoints.add(context.outputDataPoint);
		visitor.appendNewVariable(context.outputDataPoint, context.getProcessorCall());
		generator.append(" = null").eos();
		generator.addLocalVariable(Boolean.TYPE.getName(), visitor.availableVarNameOf(context.outputDataPoint),
				"false");
	}

	/**
	 * Generates the code calling a list of activators
	 *
     */
	private void visitActivators(final CallContext context) {
		final var activators = ConditionalFlowCtrl.getActivators(context.processor.getRules()).toList();
		if (activators.isEmpty()) {
			return;
		}
		generator.addLocalVariable(Boolean.TYPE.getName(), activatedVarNameOf(context.processor), "true");
		for (final var activator : activators) {
			generator.inIf(activatedVarNameOf(context.processor), gen -> {

				final var unprocessed = context.unprocessedAdapters(visitor.listAdapters(context, activator));
				visitor.visitExternalAdapters(context, unprocessed);
				context.processedAdapters.addAll(unprocessed);

				gen.appendIndented(activatedVarNameOf(context.processor)).append(" &= ");
				visitor.appendCall(context, activator);
			});

		}
	}
}
