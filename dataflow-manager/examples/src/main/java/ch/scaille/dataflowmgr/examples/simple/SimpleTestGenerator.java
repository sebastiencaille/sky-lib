package ch.scaille.dataflowmgr.examples.simple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

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
import ch.scaille.generators.util.CodeGeneratorParams;
import ch.scaille.generators.util.GenerationMetadata;
import ch.scaille.generators.util.Template;
import ch.scaille.util.helpers.ClassFinder;
import ch.scaille.util.helpers.Logs;

/**
 * This test case generates the java and dot flows
 *
 * @author scaille
 */
public class SimpleTestGenerator {

	private static final String SIMPLE_SERVICE_PKG = "ch.scaille.dataflowmgr.examples.simple";

	private static final String DP_COMPLETE = "complete";

	public static final String SIMPLE_SERVICE_CLASS = SIMPLE_SERVICE_PKG + ".SimpleService";
	public static final String SIMPLE_FLOW_CONDITIONS_CLASS = SIMPLE_SERVICE_PKG + ".SimpleFlowConditions";
	public static final String SIMPLE_EXTERNAL_ADAPTER_CLASS = SIMPLE_SERVICE_PKG + ".SimpleExternalAdapter";

	public static void main(String[] args) throws IOException, InterruptedException {
		final var targetFolder = CodeGeneratorParams.mavenTarget(SimpleTestGenerator.class);
		final var targetPathSrc = targetFolder.resolve("generated-tests").toAbsolutePath();
		final var targetPathDot = targetFolder.resolve("reports");
		final var generationMetadata = GenerationMetadata.fromCommandLine(SimpleTestGenerator.class, args);

		final var dictionary = JavaToDictionary.configure(ClassFinder.ofCurrentThread())
				.withPackages(SIMPLE_SERVICE_PKG)
				.scan()
				.collect(JavaToDictionary.toDictionary());

		// Services (see AbstractFlow)
		final var simpleService = dictionary.processors.map(SIMPLE_SERVICE_CLASS, "simpleService");
		final var simpleFlowConditions = (Calls<CustomCall>) dictionary.flowControl.get(Conditions.class)
				.map(SIMPLE_FLOW_CONDITIONS_CLASS, "simpleFlowConditions");
		final var simpleExternalAdapter = dictionary.externalAdapters.map(SIMPLE_EXTERNAL_ADAPTER_CLASS,
				"simpleExternalAdapter");

		// Processors
		final var initCall = simpleService.get("init");
		final var completeCall = simpleService.get(DP_COMPLETE);
		final var keepAsIsCall = simpleService.get("keepAsIs");

		// Conditions
		final var mustCompleteCond = simpleFlowConditions.get("mustComplete");

		// Eternal adapters
		final var getCompletionCall = simpleExternalAdapter.get("getCompletion");
		final var displayDataCall = simpleExternalAdapter.get("display");

		// Bindings
		final var entryToInitCall = Binding.builder(Flow.ENTRY_POINT, initCall);
		final var initToCompleteCall = Binding.builder(initCall, completeCall)
				.withExternalData(getCompletionCall)
				.as(DP_COMPLETE);
		final var initToKeepAsIsCall = Binding.builder(initCall, keepAsIsCall).as(DP_COMPLETE);
		final var completeToExitCall = Binding.builder(DP_COMPLETE, Flow.EXIT_POINT).withExternalData(displayDataCall);

		// Flow
		final var flow = Flow.builder("SimpleFlowTest", UUID.randomUUID(), "java.lang.String") //
				.add(entryToInitCall) //
				.add(ConditionalFlowCtrl.builder("CompleteData") //
						.conditional(mustCompleteCond, initToCompleteCall)
						.fallback(initToKeepAsIsCall)) //
				.add(completeToExitCall)
				.build();

		Files.createDirectories(targetPathSrc);

		// Generate the procedural flow
		new FlowToProceduralJavaVisitor(flow, SIMPLE_SERVICE_PKG,
				Template.from("templates/flow.template").withGenerationMetadata(generationMetadata)).process()
				.writeToFolder(targetPathSrc);

		// Generate the reactive flow
		new FlowToRXJavaVisitor(flow, "ch.scaille.dataflowmgr.examples.simplerx",
				Template.from("templates/flowrx.template").withGenerationMetadata(generationMetadata), true).process()
				.writeToFolder(targetPathSrc);

		// Generate the graphic

		Files.createDirectories(targetPathDot);
		Files.write(targetPathDot.resolve("SimpleFlow.dot"),
				new FlowToDotVisitor(flow).process().getOutput().getUTF8());
		runDot(targetPathDot);
	}

	private static void runDot(Path targetPathDot) throws IOException, InterruptedException {
		final Process dotExec = new ProcessBuilder("dot", "-Tpng", "-o" + targetPathDot.resolve("SimpleFlow.png"),
				targetPathDot.resolve("SimpleFlow.dot").toString()).start();
		try (var es = new InputStreamReader(dotExec.getErrorStream(), StandardCharsets.UTF_8);
				var ls = Logs.streamOf(SimpleTestGenerator.class, Level.INFO)) {
			es.transferTo(ls);
		}
		final int dotExit = dotExec.waitFor();
		if (dotExit != 0) {
			throw new IllegalStateException("Png generation failed");
		}
	}

}
