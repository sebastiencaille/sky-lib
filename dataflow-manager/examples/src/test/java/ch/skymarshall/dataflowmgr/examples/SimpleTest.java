package ch.skymarshall.dataflowmgr.examples;

import static ch.skymarshall.dataflowmgr.model.Binding.builder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import ch.skymarshall.dataflowmgr.generator.JavaToDictionary;
import ch.skymarshall.dataflowmgr.generator.writers.dot.FlowToDotVisitor;
import ch.skymarshall.dataflowmgr.generator.writers.java.FlowToProceduralJavaVisitor;
import ch.skymarshall.dataflowmgr.model.Dictionary;
import ch.skymarshall.dataflowmgr.model.Flow;
import ch.skymarshall.dataflowmgr.model.Processor;
import ch.skymarshall.util.generators.Template;

public class SimpleTest {

	@Test
	public void testFlow() throws IOException {

		final Dictionary serviceDictionary = new JavaToDictionary().scan("ch.skymarshall.dataflowmgr.examples.simple");
		serviceDictionary.mapToService("ch.skymarshall.dataflowmgr.examples.simple.SimpleService", "simpleService");

		final Processor init = serviceDictionary.getProcessor("simpleService.init");
		final Processor enhance1 = serviceDictionary.getProcessor("simpleService.enhance1");
		final Processor enhance2 = serviceDictionary.getProcessor("simpleService.enhance2");
		final Processor display = serviceDictionary.getProcessor("simpleService.display");

		final Flow flow = new Flow("SimpleFlow", UUID.randomUUID(), init);
		flow.add("enhance1", enhance1).add("enhance2", enhance2).add("display", display);

		flow.add(builder(Flow.ENTRY_PROCESSOR, "enhance1").build());
		flow.add(builder("enhance1", "display").build());

		flow.add(builder(Flow.ENTRY_PROCESSOR, "enhance2").build());
		flow.add(builder("enhance2", "display").build());

		new FlowToProceduralJavaVisitor(flow, "ch.skymarshall.dataflowmgr.examples.simple",
				Template.from("templates/flow.template")).process().writeToFolder(new File("src/test/java"));

		try (final FileWriter out = new FileWriter(new File("src/test/resources/SimpleFlow.dot"))) {
			out.write(new FlowToDotVisitor(flow).process().getOutput().toString());
		}

	}

}
