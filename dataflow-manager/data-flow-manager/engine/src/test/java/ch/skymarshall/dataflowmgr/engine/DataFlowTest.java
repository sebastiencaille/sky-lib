package ch.skymarshall.dataflowmgr.engine;

import static ch.skymarshall.dataflowmgr.engine.DecisionRule.apply;
import static ch.skymarshall.dataflowmgr.engine.DecisionRule.switchFlow;

import org.junit.Assert;
import org.junit.Test;

import ch.skymarshall.dataflowmgr.engine.data.ContextConverters;
import ch.skymarshall.dataflowmgr.engine.data.GreetingsCtxt;
import ch.skymarshall.dataflowmgr.engine.data.GreetingsService;
import ch.skymarshall.dataflowmgr.engine.data.Holder;
import ch.skymarshall.dataflowmgr.engine.data.Mood;

public class DataFlowTest {

	@Test
	@SuppressWarnings("unchecked")
	public void testNominal() {

		final GreetingsService greetingsSvc = new GreetingsService();

		// Makes hello or goodbye
		final DecisionPoint<GreetingsCtxt> prepareGreetings = new DecisionPoint<GreetingsCtxt>("Hello or Goodbye").add(
				new DecisionRule<>(apply(ctxt -> greetingsSvc.goodMood(ctxt.getMood()), GreetingsCtxt::setGreetings),
						(ctxt) -> ctxt.getMood() == Mood.GOOD),
				new DecisionRule<>(apply(ctxt -> greetingsSvc.badMood(ctxt.getMood()), GreetingsCtxt::setGreetings),
						(ctxt) -> ctxt.getMood() == Mood.BAD));

		// Add world or kitty
		final DecisionPoint<GreetingsCtxt> identifyTarget = new DecisionPoint<GreetingsCtxt>("World or Kitty").add(//
				DecisionRule.<GreetingsCtxt>either(ctxt -> ctxt.getGreetings().isEvil(), //
						apply(ctxt -> greetingsSvc.kitty(ctxt.getGreetings()), GreetingsCtxt::setResult), //
						apply(ctxt -> greetingsSvc.world(ctxt.getGreetings()), GreetingsCtxt::setResult)));

		// Hold String result
		final Holder<String> result = new Holder<>();
		final DecisionPoint<GreetingsCtxt> accumulateResult = new DecisionPoint<GreetingsCtxt>("Save result")
				.add(new DecisionRule<>(apply(ctxt -> result.set(ctxt.getResult()))));

		// Define flow
		final Flow<GreetingsCtxt>.FlowExecution flowExecution = new Flow<GreetingsCtxt>("Main").add(prepareGreetings)
				.nextStep().add(identifyTarget).nextStep().add(accumulateResult).build();

		// Run
		GreetingsCtxt ctxt;
		final ExecutionReport report = new ExecutionReport();

		ctxt = new GreetingsCtxt(Mood.GOOD);
		flowExecution.execute(ctxt, report);
		Assert.assertEquals("Hello world", result.get());

		ctxt = new GreetingsCtxt(Mood.BAD);
		flowExecution.execute(ctxt, report);
		Assert.assertEquals("Goodbye kitty", result.get());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSubFlow() {

		final GreetingsService greetingsSvc = new GreetingsService();

		// Hold the result
		final Holder<String> result = new Holder<>();
		final DecisionPoint<GreetingsCtxt> accumulateResult = new DecisionPoint<GreetingsCtxt>("Save result").add(//
				new DecisionRule<>(apply(ctxt -> result.set(ctxt.getResult()))));

		// Define sub flow

		final DecisionPoint<GreetingsCtxt> greetings = new DecisionPoint<GreetingsCtxt>("Greetings").add( //
				new DecisionRule<>(apply(ctxt -> greetingsSvc.greet(), GreetingsCtxt::setResult)));

		final Flow<GreetingsCtxt> greetFlow = new Flow<GreetingsCtxt>("Sub").add(greetings).nextStep()
				.add(accumulateResult);

		// Define main flow
		final DecisionPoint<GreetingsCtxt> prepare = new DecisionPoint<GreetingsCtxt>("Hello or Goodbye").add( //
				new DecisionRule<>(apply(ctxt -> greetingsSvc.goodMood(ctxt.getMood()), GreetingsCtxt::setGreetings),
						ctxt -> ctxt.getMood() == Mood.GOOD),
				new DecisionRule<>(switchFlow(greetFlow, ContextConverters::copy), ctxt -> ctxt.getMood() == Mood.BAD));

		final DecisionPoint<GreetingsCtxt> identifyTarget = new DecisionPoint<GreetingsCtxt>("World or Kitty").add( //
				DecisionRule.<GreetingsCtxt>either(ctxt -> ctxt.getGreetings().isEvil(),
						apply(ctxt -> greetingsSvc.kitty(ctxt.getGreetings()), GreetingsCtxt::setResult),
						apply(ctxt -> greetingsSvc.world(ctxt.getGreetings()), GreetingsCtxt::setResult)));

		final Flow<GreetingsCtxt>.FlowExecution flowExecution = new Flow<GreetingsCtxt>("Main").add(prepare).nextStep()
				.add(identifyTarget).nextStep().add(accumulateResult).build();

		// Run
		GreetingsCtxt ctxt;
		final ExecutionReport report = new ExecutionReport();

		ctxt = new GreetingsCtxt(Mood.GOOD);
		flowExecution.execute(ctxt, report);
		Assert.assertEquals("Hello world", result.get());

		ctxt = new GreetingsCtxt(Mood.BAD);
		flowExecution.execute(ctxt, report);
		Assert.assertEquals("Greetings", result.get());

	}

}
