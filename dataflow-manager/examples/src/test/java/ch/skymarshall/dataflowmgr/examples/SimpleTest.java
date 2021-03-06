package ch.skymarshall.dataflowmgr.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ch.skymarshall.dataflowmgr.annotations.Conditions;
import ch.skymarshall.dataflowmgr.generator.dictionary.java.JavaToDictionary;
import ch.skymarshall.dataflowmgr.generator.writers.dot.FlowToDotVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.javaproc.FlowToProceduralJavaVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.javarx.FlowToRXJavaVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.Binding.Builder;
import ch.skymarshall.dataflowmgr.model.CustomCall;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.Dictionary.Calls;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.skymarshall.util.generators.Template;
import ch.skymarshall.util.helpers.Log;

/**
 * This test case generates the java and dot flows
 * 
 * @author scaille
 *
 */
public class SimpleTest {

	private static final String DP_Complete = "complete";

	public static final String SIMPLE_SERVICE_PKG = "ch.skymarshall.dataflowmgr.examples.simple";

	public static final String SIMPLE_SERVICE_CLASS = SIMPLE_SERVICE_PKG + ".SimpleService";
	public static final String SIMPLE_FLOW_CONDITIONS_CLASS = SIMPLE_SERVICE_PKG + ".SimpleFlowConditions";
	public static final String SIMPLE_EXTERNAL_ADAPTER_CLASS = SIMPLE_SERVICE_PKG + ".SimpleExternalAdapter";

	@Test
	public void testFlow() throws IOException, InterruptedException {

		final Dictionary dictionary = new JavaToDictionary().scan("ch.skymarshall.dataflowmgr.examples.simple");

		// Services (see AbstractFlow)
		final Calls<Processor> simpleService = dictionary.processors.map(SIMPLE_SERVICE_CLASS, "simpleService");
		final Calls<CustomCall> simpleFlowConditions = (Calls<CustomCall>) dictionary.flowControl.get(Conditions.class)
				.map(SIMPLE_FLOW_CONDITIONS_CLASS, "simpleFlowConditions");
		final Calls<ExternalAdapter> simpleExternalAdapter = dictionary.externalAdapters
				.map(SIMPLE_EXTERNAL_ADAPTER_CLASS, "simpleExternalAdapter");

		// Processors
		final Processor init = simpleService.get("init");
		final Processor complete = simpleService.get("complete");
		final Processor keepAsIs = simpleService.get("keepAsIs");

		// Conditions
		final CustomCall mustComplete = simpleFlowConditions.get("mustComplete");

		// Eternal adapters
		final ExternalAdapter getCompletion = simpleExternalAdapter.get("getCompletion");
		final ExternalAdapter displayData = simpleExternalAdapter.get("display");

		// Bindings
		final Builder entryToInit = Binding.builder(Flow.ENTRY_POINT, init);
		final Builder initToComplete = Binding.builder(init, complete).withExternalData(getCompletion).as(DP_Complete);
		final Builder initToKeepAsIs = Binding.builder(init, keepAsIs).as(DP_Complete);
		final Builder completeToExit = Binding.builder(DP_Complete, Flow.EXIT_POINT).withExternalData(displayData);

		// Flow
		final Flow flow = Flow.builder("SimpleFlow", UUID.randomUUID(), "java.lang.String") //
				.add(entryToInit) //
				.add(ConditionalFlowCtrl.builder("CompleteData") //
						.conditional(mustComplete, initToComplete).fallback(initToKeepAsIs)) //
				.add(completeToExit).build();

		// Generate the procedural flow
		new FlowToProceduralJavaVisitor(flow, "ch.skymarshall.dataflowmgr.examples.simple",
				Template.from("templates/flow.template")).process().writeToFolder(new File("src/test/java"));

		// Generate the reactive flow
		new FlowToRXJavaVisitor(flow, "ch.skymarshall.dataflowmgr.examples.simplerx",
				Template.from("templates/flowrx.template"), true).process().writeToFolder(new File("src/test/java"));

		// Generate the graphic
		try (final FileWriter out = new FileWriter(new File("src/test/reports/SimpleFlow.dot"))) {
			out.write(new FlowToDotVisitor(flow).process().getOutput().toString());
		}
		runDot();
	}

	private void runDot() throws IOException, InterruptedException {
		final Process dotExec = new ProcessBuilder("dot", "-Tpng", "-osrc/test/reports/SimpleFlow.png",
				"src/test/reports/SimpleFlow.dot").start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(dotExec.getErrorStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			Log.of(this).info(line);
		}
		final int dotExit = dotExec.waitFor();
		assertEquals(0, dotExit);
	}

}
