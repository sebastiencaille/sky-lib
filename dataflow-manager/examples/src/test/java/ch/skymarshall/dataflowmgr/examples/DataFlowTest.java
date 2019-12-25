/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.examples;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.skymarshall.dataflowmgr.engine.examples.SimpleFlowFactory;
import ch.skymarshall.dataflowmgr.engine.examples.dto.IntTransfer;
import ch.skymarshall.dataflowmgr.engine.model.ExecutionReport;
import ch.skymarshall.dataflowmgr.engine.sequential.FlowExecution;
import ch.skymarshall.dataflowmgr.engine.sequential.MemRegistry;
import ch.skymarshall.dataflowmgr.generator.writers.dot.DotFileWriter;
import ch.skymarshall.dataflowmgr.model.Flow;

public class DataFlowTest {

	@Test
	public void testNominal() throws JsonProcessingException, IOException, InterruptedException {

		final MemRegistry registry = new MemRegistry();
		final Flow<IntTransfer> simpleFlow = SimpleFlowFactory.create(registry);
		final String jsonFlow = "data/simple-flow.json";
		final File out = new File("src/test/reports/simple-flow-report.json");
		final String flowName = "SimpleFlow";

		testFlow(registry, simpleFlow, jsonFlow, out, flowName);

	}

	@Test
	public void testBroadcast() throws JsonProcessingException, IOException, InterruptedException {

		final MemRegistry registry = new MemRegistry();
		final Flow<IntTransfer> simpleFlow = SimpleFlowFactory.create(registry);
		final String jsonFlow = "data/broadcast-flow.json";
		final File out = new File("src/test/reports/broadcast-flow-report.json");
		final String flowName = "BroadcastFlow";

		testFlow(registry, simpleFlow, jsonFlow, out, flowName);

	}

	private void testFlow(final MemRegistry registry, final Flow<IntTransfer> simpleFlow, final String jsonFlow,
			final File out, final String flowName) throws IOException, JsonProcessingException, InterruptedException {
		final ExecutionReport report = new ExecutionReport(registry);

		final IntTransfer inputData1 = new IntTransfer();
		inputData1.setIntValue(1);
		new FlowExecution<>(simpleFlow).execute(inputData1, report, registry);

		final IntTransfer inputData2 = new IntTransfer();
		inputData2.setIntValue(2);
		new FlowExecution<>(simpleFlow).execute(inputData2, report, registry);

		final ObjectMapper mapper = new ObjectMapper();
		out.getParentFile().mkdirs();
		Files.write(out.toPath(), mapper.writeValueAsString(report.getSteps()).getBytes());

		final DotFileWriter dotFileWriter = new DotFileWriter(new File("src/test/reports"));
		dotFileWriter.configure(
				() -> Thread.currentThread().getContextClassLoader().getResourceAsStream("data/config.json"), "");
		dotFileWriter.loadModule(() -> Thread.currentThread().getContextClassLoader().getResourceAsStream(jsonFlow));
		dotFileWriter.loadStepsReport(out);
		dotFileWriter.generate();

		dotFileWriter.toPng(flowName, "mainflow");
	}

}