package ch.skymarshall.dataflowmgr.examples;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.generator.JavaToDictionary;
import ch.skymarshall.dataflowmgr.generator.writers.dot.FlowToDotVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.java.FlowToProceduralJavaVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.ConditionalBindingGroup;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.Template;

public class SimpleTest {

	private static final String DP_ENHANCED = "enhanced";

	@Test
	public void testFlow() throws IOException, InterruptedException {
		final String simpleService = "ch.skymarshall.dataflowmgr.examples.simple.SimpleService";
		final String simpleExternalAdapter = "ch.skymarshall.dataflowmgr.examples.simple.SimpleExternalAdapter";

		final Dictionary dictionary = new JavaToDictionary().scan("ch.skymarshall.dataflowmgr.examples.simple");
		dictionary.mapToService(simpleService, "simpleService");
		dictionary.mapToService(simpleExternalAdapter, "simpleExternalAdapter");

		final Processor init = dictionary.getProcessor(simpleService, "init");
		final Processor enhance = dictionary.getProcessor(simpleService, "enhance");
		final Processor noEnhance = dictionary.getProcessor(simpleService, "noEnhance");

		final ExternalAdapter loadData = dictionary.getExternalAdapter(simpleExternalAdapter, "load");
		final ExternalAdapter displayData = dictionary.getExternalAdapter(simpleExternalAdapter, "display");

		final Flow flow = Flow.builder("SimpleFlow", UUID.randomUUID(), "String")//
				.add(Binding.builder(Flow.INITIAL_DATA, init))//
				.add(ConditionalBindingGroup.builder("Svc1 or Svc2")//
						.add(Binding.builder(init, enhance)//
								.withExternalAdapter(loadData).activator("simpleService.isEnhanceEnabled")//
								.as(DP_ENHANCED))//
						.add(Binding.builder(init, noEnhance)//
								.as(DP_ENHANCED))) //
				.add(Binding.builder(DP_ENHANCED, Flow.EXIT).withExternalAdapter(displayData))//
				.build();

		new FlowToProceduralJavaVisitor(flow, "ch.skymarshall.dataflowmgr.examples.simple",
				Template.from("templates/flow.template")).process().writeToFolder(new File("src/test/java"));

		try (final FileWriter out = new FileWriter(new File("src/test/resources/SimpleFlow.dot"))) {
			out.write(new FlowToDotVisitor(flow).process().getOutput().toString());
		}

		final Process dotExec = new ProcessBuilder("dot", "-Tpng", "-osrc/test/resources/SimpleFlow.png",
				"src/test/resources/SimpleFlow.dot").start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(dotExec.getErrorStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		final int dotExit = dotExec.waitFor();
		assertEquals(0, dotExit);
	}

}
