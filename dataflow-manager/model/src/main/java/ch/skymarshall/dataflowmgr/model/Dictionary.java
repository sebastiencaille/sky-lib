package ch.skymarshall.dataflowmgr.model;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class Dictionary {

	private final Map<String, Processor> processors = new HashMap<>();

	private final Map<String, ExternalInput> externalInputs = new HashMap<>();

	public void addProcessor(final Processor processor) {
		processors.put(processor.getName(), processor);
	}

	public void addExternalInput(final ExternalInput externalInput) {
		externalInputs.put(externalInput.getName(), externalInput);
	}

	public Processor getProcessor(final String name) {
		final Processor processor = processors.get(name);
		if (processor == null) {
			throw new InvalidParameterException("name: " + name);
		}
		return processor;
	}

	public void mapToService(final String from, final String to) {
		final Map<String, Processor> origProc = new HashMap<>(processors);
		origProc.entrySet().stream()
				.forEach(e -> processors.put(e.getKey().replace(from, to), e.getValue().derivate(to)));

		final Map<String, ExternalInput> origExtInp = new HashMap<>(externalInputs);
		origExtInp.entrySet().stream()
				.forEach(e -> externalInputs.put(e.getKey().replace(from, to), e.getValue().derivate(to)));

	}

}
