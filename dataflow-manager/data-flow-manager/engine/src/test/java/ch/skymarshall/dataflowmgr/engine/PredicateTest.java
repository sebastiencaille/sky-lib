package ch.skymarshall.dataflowmgr.engine;

import static ch.skymarshall.dataflowmgr.engine.DecisionRule.sink;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.engine.data.Holder;

public class PredicateTest {

	private static class Context {
		ContextObject<String> value = new ContextObject<>();

		public Context(final String value) {
			this.value.setValue(value);
		}

		public String getValue() {
			return value.getValue();
		}
	}

	@Test
	public void testEither() {

		final DecisionRule<Context>[] dr = DecisionRule.either(ctxt -> "yes".equals(ctxt.getValue()), null, null);

		final Context ctxtYes = new Context("yes");
		final Context ctxtNo = new Context("no");
		assertTrue(dr[0].test(ctxtYes));
		assertFalse(dr[0].test(ctxtNo));

		assertTrue(dr[1].test(ctxtNo));
		assertFalse(dr[1].test(ctxtYes));

	}

	@Test
	public void testDecisionPoint() {

		final Holder<Boolean> holder = new Holder<>();

		final DecisionPoint<Context> dp = new DecisionPoint<>("Test");
		dp.add(DecisionRule.either(ctxt -> "yes".equals(ctxt.getValue()), sink(ctxt -> holder.set(true)),
				sink(ctxt -> holder.set(false))));

		final Context ctxtYes = new Context("yes");
		final Context ctxtNo = new Context("no");

		holder.set(null);
		final DecisionPoint<Context>.DecisionPointExecutor executor = dp.forContext(ctxtYes, new ExecutionReport());
		assertTrue(executor.prepare());
		executor.execute();
		assertTrue(holder.get());

		holder.set(null);
		final DecisionPoint<Context>.DecisionPointExecutor executor2 = dp.forContext(ctxtNo, new ExecutionReport());
		assertTrue(executor2.prepare());
		executor2.execute();
		assertFalse(holder.get());
	}

}
