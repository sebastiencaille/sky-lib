package ch.skymarshall.dataflowmgr.engine;

import static ch.skymarshall.dataflowmgr.engine.DecisionRule.sink;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.engine.data.Holder;
import ch.skymarshall.dataflowmgr.engine.data.TestFlowFactory;
import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport;
import ch.skymarshall.dataflowmgr.model.DecisionPoint;
import ch.skymarshall.dataflowmgr.model.DecisionRule;
import ch.skymarshall.dataflowmgr.model.FlowAction;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.NoData;

public class PredicateTest {

	public static class StringFlow extends FlowData {
		public StringFlow() {
			super(TestFlowFactory.uuid(), "String");
		}

		public String data;
	}

	public static class TestResult extends FlowAction<StringFlow, NoData> {
		boolean triggered;

		@Override
		public NoData apply(final StringFlow t) {
			triggered = true;
			return null;
		}
	}

	@Test
	public void testEither() {

		final DecisionRule<StringFlow, StringFlow>[] dr = DecisionRule.either(ctxt -> "yes".equals(ctxt.getValue()),
				null, null);

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
