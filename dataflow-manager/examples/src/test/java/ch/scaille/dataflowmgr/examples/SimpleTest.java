package ch.scaille.dataflowmgr.examples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.generator.dictionary.java.JavaToDictionary;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor;
import ch.scaille.dataflowmgr.generator.writers.javaproc.FlowToProceduralJavaVisitor;
import ch.scaille.dataflowmgr.generator.writers.javarx.FlowToRXJavaVisitor;
import ch.scaille.dataflowmgr.model.Binding;
import ch.scaille.dataflowmgr.model.CustomCall;
import ch.scaille.dataflowmgr.model.Dictionary.Calls;
import ch.scaille.dataflowmgr.model.Flow;
import ch.scaille.dataflowmgr.model.flowctrl.ConditionalFlowCtrl;
import ch.scaille.generators.util.Template;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.Logs;

/**
 * This test case generates the java and dot flows
 * 
 * @author scaille
 *
 */
class SimpleTest {

	private static final String DP_Complete = "complete";

	public static final String SIMPLE_SERVICE_PKG = "ch.scaille.dataflowmgr.examples.simple";

	public static final String SIMPLE_SERVICE_CLASS = SIMPLE_SERVICE_PKG + ".SimpleService";
	public static final String SIMPLE_FLOW_CONDITIONS_CLASS = SIMPLE_SERVICE_PKG + ".SimpleFlowConditions";
	public static final String SIMPLE_EXTERNAL_ADAPTER_CLASS = SIMPLE_SERVICE_PKG + ".SimpleExternalAdapter";

	@Test
	void testFlow() throws IOException, InterruptedException {

		final var dictionary = JavaToDictionary.configure(ClassFinder.forApp())
				.withPackages("ch.scaille.dataflowmgr.examples.simple").scan().collect(JavaToDictionary.toDictionary());

		// Services (see AbstractFlow)
		final var simpleService = dictionary.processors.map(SIMPLE_SERVICE_CLASS, "simpleService");
		final var simpleFlowConditions = (Calls<CustomCall>) dictionary.flowControl.get(Conditions.class)
				.map(SIMPLE_FLOW_CONDITIONS_CLASS, "simpleFlowConditions");
		final var simpleExternalAdapter = dictionary.externalAdapters.map(SIMPLE_EXTERNAL_ADAPTER_CLASS,
				"simpleExternalAdapter");

		// Processors
		final var init = simpleService.get("init");
		final var complete = simpleService.get("complete");
		final var keepAsIs = simpleService.get("keepAsIs");

		// Conditions
		final CustomCall mustComplete = simpleFlowConditions.get("mustComplete");

		// Eternal adapters
		final var getCompletion = simpleExternalAdapter.get("getCompletion");
		final var displayData = simpleExternalAdapter.get("display");

		// Bindings
		final var entryToInit = Binding.builder(Flow.ENTRY_POINT, init);
		final var initToComplete = Binding.builder(init, complete).withExternalData(getCompletion).as(DP_Complete);
		final var initToKeepAsIs = Binding.builder(init, keepAsIs).as(DP_Complete);
		final var completeToExit = Binding.builder(DP_Complete, Flow.EXIT_POINT).withExternalData(displayData);

		// Flow
		final var flow = Flow.builder("SimpleFlow", UUID.randomUUID(), "java.lang.String") //
				.add(entryToInit) //
				.add(ConditionalFlowCtrl.builder("CompleteData") //
						.conditional(mustComplete, initToComplete).fallback(initToKeepAsIs)) //
				.add(completeToExit).build();

		// Generate the procedural flow
		new FlowToProceduralJavaVisitor(flow, "ch.scaille.dataflowmgr.examples.simple",
				Template.from("templates/flow.template")).process().writeToFolder(Paths.get("src/test/java"));

		// Generate the reactive flow
		new FlowToRXJavaVisitor(flow, "ch.scaille.dataflowmgr.examples.simplerx",
				Template.from("templates/flowrx.template"), true).process().writeToFolder(Paths.get("src/test/java"));

		// Generate the graphic
		try (final var out = new FileWriter(new File("src/test/reports/SimpleFlow.dot"))) {
			out.write(new FlowToDotVisitor(flow).process().getOutput().toString());
		}
		runDot();
	}

	private void runDot() throws IOException, InterruptedException {
		final Process dotExec = new ProcessBuilder("dot", "-Tpng", "-osrc/test/reports/SimpleFlow.png",
				"src/test/reports/SimpleFlow.dot").start();
		final var reader = new BufferedReader(new InputStreamReader(dotExec.getErrorStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			Logs.of(this).info(line);
		}
		final int dotExit = dotExec.waitFor();
		assertEquals(0, dotExit);
	}

}
