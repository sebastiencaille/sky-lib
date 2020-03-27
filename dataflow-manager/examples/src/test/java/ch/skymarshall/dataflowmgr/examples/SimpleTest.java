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
import ch.skymarshall.dataflowmgr.model.Condition;
import ch.skymarshall.dataflowmgr.model.ConditionalBindingGroup;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.Dictionary.Calls;
import ch.skymarshall.dataflowmgr.model.ExternalAdapter;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.Template;

public class SimpleTest {

	private static final String DP_ENHANCED = "enhanced";

	@Test
	public void testFlow() throws IOException, InterruptedException {
		final String simpleServiceClass = "ch.skymarshall.dataflowmgr.examples.simple.SimpleService";
		final String simpleServiceConditionClass = "ch.skymarshall.dataflowmgr.examples.simple.SimpleServiceConditions";
		final String simpleExternalAdapterClass = "ch.skymarshall.dataflowmgr.examples.simple.SimpleExternalAdapter";

		//
		final Dictionary dictionary = new JavaToDictionary().scan("ch.skymarshall.dataflowmgr.examples.simple");
		final Calls<Processor> simpleService = dictionary.processors.map(simpleServiceClass, "simpleService");
		final Calls<Condition> simpleServiceConditions = dictionary.conditions.map(simpleServiceConditionClass,
				"simpleServiceConditions");
		final Calls<ExternalAdapter> simpleExternalAdapter = dictionary.externalAdapters.map(simpleExternalAdapterClass,
				"simpleExternalAdapter");

		final Processor init = simpleService.get("init");
		final Processor enhance = simpleService.get("enhance");
		final Processor noEnhance = simpleService.get("noEnhance");

		final Condition isEnhanceEnabled = simpleServiceConditions.get("isEnhanceEnabled");

		final ExternalAdapter loadData = simpleExternalAdapter.get("load");
		final ExternalAdapter displayData = simpleExternalAdapter.get("display");

		final Flow flow = Flow.builder("SimpleFlow", UUID.randomUUID(), "String")//
				.add(Binding.builder(Flow.INITIAL_DATAPOINT, init))//
				.add(ConditionalBindingGroup.builder("Enhanced ?")//
						.add(Binding.builder(init, enhance)//
								.withExternalAdapter(loadData)//
								.activator(isEnhanceEnabled)//
								.as(DP_ENHANCED))//
						.add(Binding.builder(init, noEnhance)//
								.as(DP_ENHANCED))) //
				.add(Binding.builder(DP_ENHANCED, Flow.EXIT)//
						.withExternalAdapter(displayData))//
				.build();

		new FlowToProceduralJavaVisitor(flow, "ch.skymarshall.dataflowmgr.examples.simple",
				Template.from("templates/flow.template")).process().writeToFolder(new File("src/test/java"));

		try (final FileWriter out = new FileWriter(new File("src/test/reports/SimpleFlow.dot"))) {
			out.write(new FlowToDotVisitor(flow).process().getOutput().toString());
		}

		final Process dotExec = new ProcessBuilder("dot", "-Tpng", "-osrc/test/reports/SimpleFlow.png",
				"src/test/reports/SimpleFlow.dot").start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(dotExec.getErrorStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		final int dotExit = dotExec.waitFor();
		assertEquals(0, dotExit);
	}

}
