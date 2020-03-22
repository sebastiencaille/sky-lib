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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Flow extends WithId {

	public static final String ENTRY_PROCESSOR = "entryProcessor";
	public static final String EXIT_PROCESSOR = "exitProcessor";

	public static class FlowBuilder {
		final String flowName;
		final UUID uuid;
		final Map<String, Processor> processors = new HashMap<>();
		final Map<String, ExternalAdapter> adapters = new HashMap<>();

		public FlowBuilder(final String flowName, final UUID uuid, final Processor entryProcessor) {
			this.flowName = flowName;
			this.uuid = uuid;
			processors.put(ENTRY_PROCESSOR, entryProcessor);
			processors.put(EXIT_PROCESSOR, new Processor(EXIT_PROCESSOR, "", null, Void.TYPE.toString()));
		}

		public FlowBuilder add(final String name, final Processor processor) {
			if (processors.put(name, processor) != null) {
				throw new IllegalStateException("There is already a processor with such name: " + name);
			}
			return this;
		}

		public FlowBuilder add(final String name, final ExternalAdapter adapter) {
			if (adapters.put(name, adapter) != null) {
				throw new IllegalStateException("There is already an adapter with such name: " + name);
			}
			return this;
		}

		public BindingsBuilder bindings() {
			return new BindingsBuilder(this);
		}
	}

	public static FlowBuilder builder(final String flowName, final UUID uuid, final Processor entryProcessor) {
		return new FlowBuilder(flowName, uuid, entryProcessor);
	}

	public static class BindingsBuilder {

		private final List<Binding> bindings = new ArrayList<>();
		private final IdentityHashMap<Binding, Set<Binding>> dependencies = new IdentityHashMap<>();
		private final FlowBuilder flowConfig;

		public BindingsBuilder(final FlowBuilder processorBuilder) {
			this.flowConfig = processorBuilder;
		}

		public BindingsBuilder add(final Binding.Builder binding) {
			return add(binding.build());
		}

		public BindingsBuilder add(final Binding binding) {
			validate(binding);
			bindings.add(binding);
			return this;
		}

		public BindingsBuilder add(final ConditionalBindingGroup.Builder binding) {
			return add(binding.build());
		}

		public BindingsBuilder add(final ConditionalBindingGroup caseControl) {
			caseControl.getBindings().forEach(this::add);
			final Optional<Binding> defaultBinding = caseControl.getDefaultBinding();
			if (defaultBinding.isPresent()) {
				// make default depend on all other bindings
				final Set<Binding> defaultBindingDeps = dependencies.computeIfAbsent(defaultBinding.get(),
						b -> new HashSet<>());
				defaultBindingDeps.addAll(caseControl.getBindings());
				defaultBindingDeps.remove(defaultBinding.get());
			}
			return this;
		}

		private void validate(final Binding binding) {
			if (!flowConfig.processors.containsKey(binding.fromProcessor())) {
				throw new IllegalStateException("No such processor: " + binding.fromProcessor());
			}
			if (!flowConfig.processors.containsKey(binding.toProcessor())) {
				throw new IllegalStateException("No such processor: " + binding.toProcessor());
			}
		}

		public Flow build() {
			return new Flow(this);
		}
	}

	private final BindingsBuilder config;

	public Flow(final BindingsBuilder bindingsBuilder) {
		super(bindingsBuilder.flowConfig.uuid);
		this.config = bindingsBuilder;
	}

	public String getName() {
		return config.flowConfig.flowName;
	}

	public Processor getEntryProcessor() {
		return config.flowConfig.processors.get(ENTRY_PROCESSOR);
	}

	public Processor getProcessor(final String name) {
		return config.flowConfig.processors.computeIfAbsent(name, n -> {
			throw new InvalidParameterException("No such processor:" + n);
		});
	}

	public ExternalAdapter getAdapter(final String name) {
		return config.flowConfig.adapters.computeIfAbsent(name, n -> {
			throw new InvalidParameterException("No such processor:" + n);
		});
	}

	public List<Binding> getBindings() {
		return config.bindings;
	}

	public Map<Binding, Set<Binding>> cloneDependencies() {
		return config.dependencies.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, kv -> new HashSet<>(kv.getValue())));
	}

}
