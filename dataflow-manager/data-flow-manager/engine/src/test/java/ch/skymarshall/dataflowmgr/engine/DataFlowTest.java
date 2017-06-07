/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above copyright notice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.engine;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.engine.data.DispatchJoinFlowFactory;
import ch.skymarshall.dataflowmgr.engine.data.SimpleFlowFactory;
import ch.skymarshall.dataflowmgr.engine.data.SimpleFlowFactory.IntTransfer;
import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport;
import ch.skymarshall.dataflowmgr.engine.sequential.FlowExecution;
import ch.skymarshall.dataflowmgr.engine.sequential.MemRegistry;
import ch.skymarshall.dataflowmgr.model.Flow;

public class DataFlowTest {

	@Test
	public void testNominal() {

		final MemRegistry registry = new MemRegistry();
		final Flow<IntTransfer> simpleFlow = SimpleFlowFactory.simpleFlow(registry);

		final ExecutionReport report = new ExecutionReport(registry);
		new FlowExecution<>(simpleFlow).execute(new IntTransfer(1), report, registry);
		new FlowExecution<>(simpleFlow).execute(new IntTransfer(2), report, registry);

		System.out.println(report.simpleFormat());

	}

	@Test
	public void testDispatchJoin() {
		final MemRegistry registry = new MemRegistry();
		final Flow<IntTransfer> joinFlow = DispatchJoinFlowFactory.dispatchJoinFlow(registry);

		final ExecutionReport report = new ExecutionReport(registry);
		try {
			new FlowExecution<>(joinFlow).execute(new IntTransfer(1), report, registry);
		} finally {
			System.out.println(report.simpleFormat());
		}

	}

}
