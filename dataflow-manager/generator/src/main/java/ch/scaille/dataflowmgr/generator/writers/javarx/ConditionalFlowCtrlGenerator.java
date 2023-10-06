package ch.scaille.dataflowmgr.generator.writers.javarx;

import static ch.scaille.util.text.TextFormatter.toCamelCase;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.scaille.dataflowmgr.model.Binding;
import ch.scaille.dataflowmgr.model.CustomCall;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.JavaCodeGenerator;

public class ConditionalFlowCtrlGenerator extends AbstractFlowGenerator {

	protected ConditionalFlowCtrlGenerator(FlowToRXJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		super(visitor, generator);
	}

	@Override
	public boolean matches(BindingContext context) {
		return ConditionalFlowCtrl.getCondition(context.binding.getRules()).isPresent();
	}

	@Override
	public void generate(BaseGenContext<GenContext> genContext, BindingContext context) {
		final var exclusions = ConditionalFlowCtrl.getExclusions(context.binding.getRules()).collect(toSet());
		visitor.setConditional(context.outputDataPoint);

		final var topCall = visitor.toVariable(context.binding) + "_conditional";
		flowFactories.appendIndented(
				"private Maybe<FlowExecution> %s(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks)",
				topCall).openBlock();

		flowFactories.appendIndented("final Maybe<FlowExecution> topCall = %s(execution, callModifier, callbacks)",
				genContext.getLocalContext().getTopCall()).eos();
		genContext.getLocalContext().setTopCall("topCall");

		visitActivators(context, genContext.getLocalContext());

		if (!exclusions.isEmpty()) {
			generateConditionalCallToService(genContext, context, exclusions);
		} else {
			flowFactories.appendIndented("return %s", genContext.getLocalContext().getTopCall()).eos();
		}

		flowFactories.closeBlock();

		genContext.getLocalContext().setTopCall(topCall);
		genContext.next(context);
	}

	/**
	 * Generates the code calling a list of activators
	 *
	 * @param context
	 * @param genContext
	 */
	private void visitActivators(final BindingContext context, GenContext genContext) {
		final var activators = ConditionalFlowCtrl.getActivators(context.binding.getRules()).toList();
		if (activators.isEmpty()) {
			return;
		}
		final var activatorNames = new ArrayList<String>();

		// Activators
		for (final var activator : activators) {

			final var unprocessedAdapters = context.unprocessedAdapters(visitor.listAdapters(context, activator));
			final var adapterNames = visitor.visitExternalAdapters(context, unprocessedAdapters);
			context.processedAdapters.addAll(unprocessedAdapters);

			final var activatorParameters = visitor.guessParameters(context, activator);

			activatorNames.add("activator_" + visitor.toVariable(activator));
			generateCallActivator(activator, adapterNames, activatorParameters);
		}

		// Activation check
		generateCallAllActivators(context, activatorNames);
		genContext.setTopCall("activators");
	}

	private void generateConditionalCallToService(BaseGenContext<GenContext> genContext, BindingContext context,
			final Set<Binding> exclusions) {
		flowFactories.eoli().append("return Maybe.just(execution).mapOptional(f -> ").append(exclusions.stream()
				.map(x -> "DataPointState.SKIPPED == f." + visitor.dataPointStateOf(x)).collect(joining(" && ")))
				.append("?Optional.of(f):Optional.empty())");
		if (genContext.getLocalContext().debug) {
			flowFactories.append(".doOnComplete(() -> info(\"%s: Call skipped\"))",
					visitor.toVariable(context.binding));
		}
		flowFactories.append(".doOnSuccess(f -> %s.subscribe())", genContext.getLocalContext().getTopCall()).eos();
	}

	private void generateCallActivator(final CustomCall activator, final List<String> adapterNames,
			final List<String> activatorParameters) {
		flowFactories.appendIndented("final Maybe<Boolean> activator_%s = Maybe.just(execution)",
				visitor.toVariable(activator)).indent();
		visitor.addAdapterZip(adapterNames);
		flowFactories.eoli() //
				.append(".map(f -> ").appendMethodCall("this", activator.getCall(), activatorParameters).append(")") //
				.eoli().append(".subscribeOn(Schedulers.computation())").eos().unindent().eol();
	}

	private void generateCallAllActivators(final BindingContext context, final List<String> activatorNames) {
		flowFactories.appendIndented("final Maybe<FlowExecution> activators = Maybe.just(true)").indent();
		for (final var activatorName : activatorNames) {
			flowFactories.eoli().append(".zipWith(").append(activatorName)
					.append(", (u, r) -> u.booleanValue() && r.booleanValue())");
		}
		flowFactories.eoli().append(".mapOptional(b -> b ? Optional.of(execution) : Optional.empty())") //
				.eoli().append(".flatMap(e -> topCall)") //
				.eoli()
				.append(".doOnComplete(() -> { execution.setState%s(DataPointState.TRIGGERED); execution.setState%s(DataPointState.SKIPPED); })",
						toCamelCase(visitor.varNameOf(context.binding)), toCamelCase(context.binding.toDataPoint())) //
				.eoli().append(".doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))") //
				.eos().unindent().eol();
	}
}
