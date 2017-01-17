package ch.skymarshall.dataflowmgr.engine.data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.skymarshall.dataflowmgr.model.DecisionPoint;
import ch.skymarshall.dataflowmgr.model.DecisionRule;
import ch.skymarshall.dataflowmgr.model.DecisionRule.CollectorFunction;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.FlowAction;
import ch.skymarshall.dataflowmgr.model.FlowActionType;
import ch.skymarshall.dataflowmgr.model.FlowData;
import ch.skymarshall.dataflowmgr.model.IDData;
import ch.skymarshall.dataflowmgr.model.NoData;
import ch.skymarshall.dataflowmgr.model.Registry;

public class TestFlowFactory {

	public static class InputData1 extends FlowData {
		int value1;

		public InputData1(final int val1) {
			super(newUuid(InputData1.class));
			this.value1 = val1;
		}

	}

	public static class InputData1b extends FlowData {
		int value1;

		public InputData1b(final int valInt1) {
			super(newUuid(InputData1b.class));
			this.value1 = valInt1;
		}

	}

	public static class InputData2 extends FlowData {
		int value2;
		int value2b;

		public InputData2(final int valInt2) {
			super(newUuid(InputData2.class));
			this.value2 = valInt2;
		}

		public InputData2() {
			super(newUuid(InputData2.class));
		}

	}

	public static class OutputData1And2 extends FlowData {

		private InputData1b data1b;
		private InputData2 data2;

		public OutputData1And2() {
			super(newUuid(OutputData1And2.class));
		}

		public InputData1b getData1b() {
			return data1b;
		}

		public void setData1b(final InputData1b data1b) {
			this.data1b = data1b;
		}

		public InputData2 getData2() {
			return data2;
		}

		public void setData2(final InputData2 data2) {
			this.data2 = data2;
		}
	}

	public static class SetOutputData1bOr2Action extends FlowAction<InputData1, OutputData1And2> {

		@Override
		public OutputData1And2 apply(final InputData1 input) {
			final OutputData1And2 out = new OutputData1And2();
			if (input.value1 == 1) {
				out.setData1b(new InputData1b(1));
			} else {
				out.setData2(new InputData2(2));
			}
			return out;
		}

	}

	public static class DumpInputData1b extends FlowAction<InputData1b, NoData> {

		@Override
		public NoData apply(final InputData1b t) {
			System.out.println(t.value1);
			return NO_DATA;
		}

	}

	public static class DumpInputData2 extends FlowAction<InputData2, NoData> {

		@Override
		public NoData apply(final InputData2 t) {
			System.out.println(t.value2);
			return NO_DATA;
		}

	}

	private static void registerObjects(final Registry registry, final IDData dp1, final IDData dp1b,
			final IDData dp2) {
		registry.registerObject(dp1, "dp1");
		registry.registerObject(dp1b, "dp1b");
		registry.registerObject(dp2, "dp2");

		registry.registerObject(new InputData1(0), "InputData1");
		registry.registerObject(new InputData1b(0), "InputData1b");
		registry.registerObject(new InputData2(), "InputData2");
		registry.registerObject(new OutputData1And2(), "OutputData1And2");
	}

	public static Flow<InputData1> simpleFlow(final Registry registry) {
		final DecisionPoint<InputData1, OutputData1And2> dp1 = DecisionPoint.simple(uuid(),
				new SetOutputData1bOr2Action());
		final DecisionPoint<InputData1b, ?> dp1b = DecisionPoint.terminal(uuid(), new DumpInputData1b());
		final DecisionPoint<InputData2, ?> dp2 = DecisionPoint.terminal(uuid(), new DumpInputData2());

		final DecisionRule<OutputData1And2, InputData1b> dp1ToDp1b = new DecisionRule<>(uuid(), FlowActionType.CONTINUE,
				dp1b, (d, dp, r) -> d.getData1b(), d -> d.getData1b() != null);
		final DecisionRule<OutputData1And2, InputData2> dp1ToDp2 = new DecisionRule<>(uuid(), FlowActionType.CONTINUE,
				dp2, (d, dp, r) -> d.getData2(), d -> d.getData2() != null);
		dp1.add(dp1ToDp1b, dp1ToDp2);

		registerObjects(registry, dp1, dp1b, dp2);
		registry.registerObject(dp1ToDp1b, "dp1ToDp1b");
		registry.registerObject(dp1ToDp2, "dp1ToDp2");

		return new Flow<>(uuid(), dp1);
	}

	public static List<Flow<InputData1>> joinFlow(final Registry registry) {

		final DecisionPoint<InputData1, OutputData1And2> dp1 = DecisionPoint.simple(uuid(),
				new SetOutputData1bOr2Action());
		final DecisionPoint<InputData1, OutputData1And2> dp1b = DecisionPoint.simple(uuid(),
				new SetOutputData1bOr2Action());

		final DecisionPoint<InputData2, ?> dp2 = DecisionPoint.terminal(uuid(), new DumpInputData2());
		dp2.setActivator((d) -> d.value2 != 0 && d.value2b != 0);

		// Fill value2 using dp1
		final CollectorFunction<OutputData1And2, InputData2> collectDp1ToDp2 = DecisionRule.collector(InputData2::new,
				(d, dp) -> dp.value2 = d.getData1b().value1);
		final DecisionRule<OutputData1And2, InputData2> dp1ToDp2 = new DecisionRule<>(uuid(), FlowActionType.CONTINUE,
				dp2, collectDp1ToDp2, d -> d.getData1b() != null);

		// Fill value2b using dp1b
		final CollectorFunction<OutputData1And2, InputData2> collectDp1bToDp2 = DecisionRule.collector(InputData2::new,
				(d, dp) -> dp.value2b = d.getData1b().value1);
		final DecisionRule<OutputData1And2, InputData2> dp1bToDp2 = new DecisionRule<>(uuid(), FlowActionType.CONTINUE,
				dp2, collectDp1bToDp2, d -> d.getData1b() != null);

		dp1.add(dp1ToDp2);
		dp1b.add(dp1bToDp2);

		registerObjects(registry, dp1, dp1b, dp2);
		registry.registerObject(dp1ToDp2, "dp1ToDp2");
		registry.registerObject(dp1bToDp2, "dp1bToDp2");

		return Arrays.asList(new Flow<>(uuid(), dp1), new Flow<>(uuid(), dp1b));
	}

	private static Map<Class<?>, UUID> classToUUID = new HashMap<>();

	public static UUID uuid() {
		return UUID.randomUUID();
	}

	public static UUID newUuid(final Class<?> clazz) {
		UUID uuid = classToUUID.get(clazz);
		if (uuid == null) {
			uuid = UUID.randomUUID();
			classToUUID.put(clazz, uuid);
		}
		return uuid;
	}

}
