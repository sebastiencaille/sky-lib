package ch.skymarshall.dataflowmgr.examples;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.generator.JavaToDictionary;
import ch.skymarshall.dataflowmgr.generator.writers.dot.FlowToDotVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.java.FlowToProceduralJavaVisitor;
import ch.skymarshall.dataflowmgr.model.Binding;
import ch.skymarshall.dataflowmgr.model.ConditionalBinding;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.Template;

public class SimpleTest {

	private static final String SVC_DISPLAY = "display";
	private static final String SVC_ENHANCE2 = "enhance2";
	private static final String SVC_ENHANCE1 = "enhance1";

	@Test
	public void testFlow() throws IOException, InterruptedException {

		final Dictionary serviceDictionary = new JavaToDictionary().scan("ch.skymarshall.dataflowmgr.examples.simple");
		serviceDictionary.mapToService("ch.skymarshall.dataflowmgr.examples.simple.SimpleService", "simpleService");

		final Processor init = serviceDictionary.getProcessor("simpleService.init");
		final Processor enhance1 = serviceDictionary.getProcessor("simpleService.enhance1");
		final Processor enhance2 = serviceDictionary.getProcessor("simpleService.enhance2");
		final Processor display = serviceDictionary.getProcessor("simpleService.display");

		final Flow flow = Flow.builder("SimpleFlow", UUID.randomUUID(), init)//
				.add(SVC_ENHANCE1, enhance1) //
				.add(SVC_ENHANCE2, enhance2) //
				.add(SVC_DISPLAY, display).bindings() //
				.add(ConditionalBinding.builder("Svc1 or Svc2")//
						.add(Binding.builder(Flow.ENTRY_PROCESSOR, SVC_ENHANCE1)
								.activator(Flow.ENTRY_PROCESSOR + ".equals(\"Hello\")")) //
						.add(Binding.builder(Flow.ENTRY_PROCESSOR, SVC_ENHANCE2))) //
				.add(Binding.builder(SVC_ENHANCE1, SVC_DISPLAY)) //
				.add(Binding.builder(SVC_ENHANCE2, SVC_DISPLAY)).build();

		new FlowToProceduralJavaVisitor(flow, "ch.skymarshall.dataflowmgr.examples.simple",
				Template.from("templates/flow.template")).process().writeToFolder(new File("src/test/java"));

		try (final FileWriter out = new FileWriter(new File("src/test/resources/SimpleFlow.dot"))) {
			out.write(new FlowToDotVisitor(flow).process().getOutput().toString());
		}

		final int dotExit = new ProcessBuilder("dot", "-Tpng", "-osrc/test/resources/SimpleFlow.png",
				"src/test/resources/SimpleFlow.dot").start().waitFor();
		assertEquals(0, dotExit);
	}

}
