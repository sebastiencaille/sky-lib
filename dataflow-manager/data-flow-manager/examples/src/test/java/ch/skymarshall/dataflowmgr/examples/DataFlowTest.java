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
import ch.skymarshall.dataflowmgr.model.Flow;

public class DataFlowTest {

	@Test
	public void testNominal() throws JsonProcessingException, IOException {

		final MemRegistry registry = new MemRegistry();
		final Flow<IntTransfer> simpleFlow = SimpleFlowFactory.create(registry);

		final ExecutionReport report = new ExecutionReport(registry);

		final IntTransfer inputData1 = new IntTransfer();
		inputData1.setIntValue(1);
		new FlowExecution<>(simpleFlow).execute(inputData1, report, registry);

		final IntTransfer inputData2 = new IntTransfer();
		inputData2.setIntValue(2);
		new FlowExecution<>(simpleFlow).execute(inputData2, report, registry);

		final File out = new File("src/main/reports/simple-flow-report.json");
		final ObjectMapper mapper = new ObjectMapper();
		out.getParentFile().mkdirs();
		Files.write(out.toPath(), mapper.writeValueAsString(report.getSteps()).getBytes());

	}

}
