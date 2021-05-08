package ch.skymarshall.dataflowmgr.generator.writers.javarx;

import static ch.skymarshall.util.text.TextFormatter.toCamelCase;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.skymarshall.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.CustomCall;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.skymarshall.util.generators.JavaCodeGenerator;

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
		final Set<Binding> exclusions = ConditionalFlowCtrl.getExclusions(context.binding.getRules()).collect(toSet());
		visitor.setConditional(context.outputDataPoint);

		String topCall = visitor.toVariable(context.binding) + "_conditional";
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
	 * @param availableVars @
	 */
	private void visitActivators(final BindingContext context, GenContext genContext) {
		List<CustomCall> activators = ConditionalFlowCtrl.getActivators(context.binding.getRules()).collect(toList());
		if (activators.isEmpty()) {
			return;
		}
		final List<String> activatorNames = new ArrayList<>();

		// Activators
		for (final CustomCall activator : activators) {

			final Set<ExternalAdapter> unprocessed = context
					.unprocessedAdapters(visitor.listAdapters(context, activator));
			final List<String> adapterNames = visitor.visitExternalAdapters(context, unprocessed);
			context.processedAdapters.addAll(unprocessed);

			final List<String> activatorParameters = visitor.guessParameters(context, activator);

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
			flowFactories.append(".doOnComplete(() -> Log.of(this).info(\"%s: Call skipped\"))",
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
		for (final String activatorName : activatorNames) {
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
