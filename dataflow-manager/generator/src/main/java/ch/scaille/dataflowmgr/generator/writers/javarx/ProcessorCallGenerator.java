package ch.scaille.dataflowmgr.generator.writers.javarx;

import static ch.scaille.util.text.TextFormatter.toCamelCase;

import ch.scaille.dataflowmgr.generator.writers.AbstractFlowVisitor.BindingContext;
import ch.scaille.generators.util.JavaCodeGenerator;

public class ProcessorCallGenerator extends AbstractFlowGenerator {

	protected ProcessorCallGenerator(FlowToRXJavaVisitor visitor, JavaCodeGenerator<RuntimeException> generator) {
		super(visitor, generator);
	}

	@Override
	public boolean matches(BindingContext context) {
		return true;
	}

	@Override
	public void generate(BaseGenContext<GenContext> genContext, BindingContext context) {

		final var processor = context.getProcessor();
		final var topCall = visitor.toVariable(context.binding) + "_svcCall";
		flowFactories.appendIndented(
				"private Maybe<FlowExecution> %s(FlowExecution execution, final Function<Maybe<FlowExecution>, Maybe<FlowExecution>> callModifier, Runnable... callbacks)",
				topCall).openBlock();

		// Call first to register adapter names
		final var adapterNames = visitor.visitExternalAdapters(context,
				context.unprocessedAdapters(context.bindingAdapters));

		// Call service if not excluded
		flowFactories.appendIndented("Maybe<FlowExecution> callService = Maybe.just(execution)").indent() //
				.eoli().append(".doOnSuccess(e -> e.setState%s(DataPointState.TRIGGERED))",
						toCamelCase(visitor.varNameOf(context.binding)));

		flowFactories.eoli().append(".doOnComplete(() -> execution.set")
				.append(toCamelCase(visitor.bindingStateOf(context.binding))).append("(DataPointState.SKIPPED))");

		if (!context.binding.isExit()) {
			flowFactories.eoli().append(".doOnSuccess(f -> f.set%s(", toCamelCase(context.outputDataPoint)) //
					.appendMethodCall("this", processor.getCall(), visitor.guessParameters(context, processor))
					.append("))");
		}

		if (genContext.getLocalContext().debug) {
			flowFactories.eoli().append(".doOnSuccess(r -> info(\"%s: Call success\"))", context.binding).eoli()
					.append(".doOnComplete(() -> info(\"%s: Call skipped\"))", context.binding); //
		}

		flowFactories.eoli().append(".doOnTerminate(() -> Arrays.stream(callbacks).forEach(Runnable::run))")//
				.eoli().append(".subscribeOn(Schedulers.computation())") //
				.unindent().eos();

// Refine call
		flowFactories.openIf("callModifier != null") //
				.appendIndented("callService = callModifier.apply(callService)").eos()//
				.closeBlock();
		flowFactories.appendIndented("callService = callService.subscribeOn(Schedulers.computation())").eos();

// Call adapters

		if (!adapterNames.isEmpty()) {
			flowFactories.eoli().append("final Maybe<FlowExecution> callServiceConst = callService").eos();
			flowFactories.appendIndented("return Maybe.just(execution)").indent(); //
			visitor.addAdapterZip(adapterNames);
			flowFactories.eoli().append(".flatMap(r -> callServiceConst)").eos().unindent();
		} else {
			flowFactories.appendIndented("return callService").eos();
		}

		flowFactories.closeBlock().eol();

		genContext.getLocalContext().setTopCall(topCall);
		genContext.next(context);
	}

}
