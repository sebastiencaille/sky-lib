/*******************************************************************************
 * Copyright (c) 2017 Sebastien Caille.
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms are permitted
 *  provided that the above Copyrightnotice and this paragraph are
 *  duplicated in all such forms and that any documentation,
 *  advertising materials, and other materials related to such
 *  distribution and use acknowledge that the software was developed
 *  by Sebastien Caille.  The name of Sebastien Caille may not be used to endorse or promote products derived
 *  from this software without specific prior written permission.
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND WITHOUT ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 ******************************************************************************/
package ch.skymarshall.dataflowmgr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Flow extends IDItem {
	public static final String ENTRY_PROCESSOR = "entryProcessor";

	private final String name;
	private final List<Binding> bindings = new ArrayList<>();
	private final Map<String, Processor> processors = new HashMap<>();

	public Flow(final String name, final UUID uuid, final Processor entryProcessor) {
		super(uuid);
		this.name = name;
		processors.put(ENTRY_PROCESSOR, entryProcessor);
	}

	public String getName() {
		return name;
	}

	public Processor getEntryProcessor() {
		return processors.get(ENTRY_PROCESSOR);
	}

	public Flow add(final String name, final Processor processor) {
		if (processors.put(name, processor) != null) {
			throw new IllegalStateException("There is already a processor with such name: " + name);
		}
		return this;
	}

	public Processor getProcessor(final String name) {
		return processors.get(name);
	}

	public Flow add(final Binding binding) {
		if (!processors.containsKey(binding.fromProcessor())) {
			throw new IllegalStateException("No such processor: " + binding.fromProcessor());
		}
		if (!processors.containsKey(binding.toProcessor())) {
			throw new IllegalStateException("No such processor: " + binding.toProcessor());
		}
		bindings.add(binding);
		return this;
	}

	public List<Binding> getBindings() {
		return bindings;
	}

}
