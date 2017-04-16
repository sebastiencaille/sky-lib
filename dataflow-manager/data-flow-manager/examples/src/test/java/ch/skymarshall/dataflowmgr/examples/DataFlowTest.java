package ch.skymarshall.dataflowmgr.examples;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.engine.examples.SimpleFlowFactory;
import ch.skymarshall.dataflowmgr.engine.examples.dto.IntTransfer;
import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport;
import ch.skymarshall.dataflowmgr.engine.sequential.FlowExecution;
import ch.skymarshall.dataflowmgr.engine.sequential.MemRegistry;
import ch.skymarshall.dataflowmgr.model.Flow;

public class DataFlowTest {

	@Test
	public void testNominal() {

		final MemRegistry registry = new MemRegistry();
		final Flow<IntTransfer> simpleFlow = SimpleFlowFactory.create(registry);

		final ExecutionReport report = new ExecutionReport(registry);

		final IntTransfer inputData1 = new IntTransfer();
		inputData1.setIntValue(1);
		new FlowExecution<>(simpleFlow).execute(inputData1, report, registry);

		final IntTransfer inputData2 = new IntTransfer();
		inputData2.setIntValue(2);
		new FlowExecution<>(simpleFlow).execute(inputData2, report, registry);

		System.out.println(report.simpleFormat());

	}

}
