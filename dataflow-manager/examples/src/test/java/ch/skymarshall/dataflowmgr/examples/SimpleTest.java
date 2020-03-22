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

	private static final String SVC_ENHANCE = "enhance";
	private static final String SVC_NO_ENHANCE = "noEnhance";

	private static final String ADAPTER_LOAD_DATA = "loadData";
	private static final String ADAPTER_DISPLAY_DATA = "displayData";

	@Test
	public void testFlow() throws IOException, InterruptedException {

		final Dictionary serviceDictionary = new JavaToDictionary().scan("ch.skymarshall.dataflowmgr.examples.simple");
		serviceDictionary.mapToService("ch.skymarshall.dataflowmgr.examples.simple.SimpleService", "simpleService");
		serviceDictionary.mapToService("ch.skymarshall.dataflowmgr.examples.simple.SimpleExternalAdapter",
				"simpleExternalAdapter");

		final Processor init = serviceDictionary.getProcessor("simpleService.init");
		final Processor enhance = serviceDictionary.getProcessor("simpleService.enhance");
		final Processor noEnhance = serviceDictionary.getProcessor("simpleService.noEnhance");

		final ExternalAdapter loadData = serviceDictionary.getExternalAdapter("simpleExternalAdapter.load");
		final ExternalAdapter displayData = serviceDictionary.getExternalAdapter("simpleExternalAdapter.display");

		final Flow flow = Flow.builder("SimpleFlow", UUID.randomUUID(), init)//
				.add(SVC_ENHANCE, enhance) //
				.add(SVC_NO_ENHANCE, noEnhance) //
				.add(ADAPTER_LOAD_DATA, loadData) //
				.add(ADAPTER_DISPLAY_DATA, displayData) //
				.bindings()//
				.add(ConditionalBindingGroup.builder("Svc1 or Svc2")//
						.add(Binding.entryBuilder(SVC_ENHANCE).withExternalAdapter(ADAPTER_LOAD_DATA)
								.activator("simpleService.isEnhanceEnabled"))//
						.add(Binding.entryBuilder(SVC_NO_ENHANCE))) //
				.add(Binding.exitBuilder(SVC_ENHANCE).withExternalAdapter(ADAPTER_DISPLAY_DATA)) //
				.add(Binding.exitBuilder(SVC_NO_ENHANCE).withExternalAdapter(ADAPTER_DISPLAY_DATA)).build();

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
