package ch.scaille.dataflowmgr.examples.simple;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

import ch.scaille.dataflowmgr.annotations.Conditions;
import ch.scaille.dataflowmgr.examples.simple.annotations.ExampleApis;
import ch.scaille.dataflowmgr.generator.dictionary.java.JavaToDictionary;
import ch.scaille.dataflowmgr.generator.writers.dot.FlowToDotVisitor;
import ch.scaille.dataflowmgr.generator.writers.javaproc.FlowToProceduralJavaVisitor;
import ch.scaille.dataflowmgr.generator.writers.javarx.FlowToRXJavaVisitor;
import ch.scaille.dataflowmgr.model.Processor;
import ch.scaille.dataflowmgr.model.GenericCall;
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

	private static final String SIMPLE_EXAMPLE_PACKAGE = "ch.scaille.dataflowmgr.examples.simple";
	public static final String CONDITIONAL_MUTATION = "conditionalMutation";

	static void main(String[] args) throws IOException, InterruptedException {
		final var targetFolder = CodeGeneratorParams.mavenTargetFolderOf(SimpleTestGenerator.class);
		final var targetPathSrc = targetFolder.resolve("generated-test-sources/data-flow").toAbsolutePath();
		final var targetPathDot = targetFolder.resolve("reports");
		final var generationMetadata = GenerationMetadata.fromCommandLine(SimpleTestGenerator.class, args);

		final var dictionary = JavaToDictionary.configure(ClassFinder.ofCurrentThread())
				.withPackages(SIMPLE_EXAMPLE_PACKAGE)
				.scan()
				.collect(JavaToDictionary.toDictionary());

		// Services (see AbstractFlow)
		final var simpleService = dictionary.processors.map(ExampleApis.SIMPLE_SERVICE);
		final var simpleFlowConditions = (Calls<GenericCall>) dictionary.flowControl.get(Conditions.class)
				.map(ExampleApis.SIMPLE_FLOW_CONDITIONS);
		final var simpleExternalAdapter = dictionary.externalAdapters.map(ExampleApis.SIMPLE_EXTERNAL_ADAPTER);

		// Processors
		final var initCall = simpleService.get(ExampleApis.INIT);
		final var mutateCall = simpleService.get(ExampleApis.MUTATE);
		final var keepAsIsCall = simpleService.get(ExampleApis.KEEP_AS_IS);

		// Conditions
		final var mustMutateCond = simpleFlowConditions.get(ExampleApis.MUST_MUTATE);

		// Eternal adaptersDP_
		final var getMutationCall = simpleExternalAdapter.get(ExampleApis.GET_MUTATION);
		final var displayDataCall = simpleExternalAdapter.get(ExampleApis.DISPLAY);

		// Calls
		final var entryToInitCall = Processor.builder(Flow.ENTRY_POINT, initCall);
		final var initToMutateCall = Processor.builder(initCall, mutateCall)
				.withExternalData(getMutationCall)
				.as(CONDITIONAL_MUTATION);
		final var initToKeepAsIsCall = Processor.builder(initCall, keepAsIsCall).as(CONDITIONAL_MUTATION);
		final var mutateToExitCall = Processor.builder(CONDITIONAL_MUTATION, Flow.EXIT_POINT).withExternalData(displayDataCall);

		// Flow
		final var flow = Flow.builder("SimpleFlowTest", UUID.randomUUID(), String.class.getName()) //
				.add(entryToInitCall) //DP_
				.add(ConditionalFlowCtrl.builder("CompleteData") //
						.conditional(mustMutateCond, initToMutateCall)
						.fallback(initToKeepAsIsCall)) //
				.add(mutateToExitCall)
				.build();

		Files.createDirectories(targetPathSrc);

		// Generate the procedural flow
		new FlowToProceduralJavaVisitor(flow, SIMPLE_EXAMPLE_PACKAGE,
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
