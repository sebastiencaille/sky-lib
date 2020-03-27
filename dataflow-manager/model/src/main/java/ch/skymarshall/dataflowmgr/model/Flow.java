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
import java.util.Collections;
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

	public static class FlowBuilder {
		final String flowName;
		final UUID uuid;
		private final List<Binding> bindings = new ArrayList<>();
		private final IdentityHashMap<Binding, Set<Binding>> dependencies = new IdentityHashMap<>();
		private final Map<Processor, List<Binding>> leafsByProcessor = new HashMap<>();
		private final String inputType;

		public FlowBuilder(final String flowName, final UUID uuid, final String inputType) {
			this.flowName = flowName;
			this.uuid = uuid;
			this.inputType = inputType;
		}

		public FlowBuilder add(final Binding.Builder binding) {
			add(binding.build(leafsByProcessor));
			return this;
		}

		public FlowBuilder add(final Binding binding) {
			bindings.add(binding);
			return this;
		}

		public FlowBuilder add(final ConditionalBindingGroup.Builder binding) {
			return add(binding.build());
		}

		public FlowBuilder add(final ConditionalBindingGroup caseControl) {
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

		public Flow build() {
			return new Flow(this);
		}
	}

	public static FlowBuilder builder(final String flowName, final UUID uuid, final String inputType) {
		return new FlowBuilder(flowName, uuid, inputType);
	}

	private final FlowBuilder config;

	public static final String EXIT_PROCESSOR = "exit";
	public static final Processor EXIT = new Processor("exit", "exit", Collections.emptyList(), Void.TYPE.getName());
	public static final String INITIAL_DATAPOINT = "inputDataPoint";

	public Flow(final FlowBuilder bindingsBuilder) {
		super(bindingsBuilder.uuid);
		this.config = bindingsBuilder;
	}

	public String getName() {
		return config.flowName;
	}

	public List<Binding> getBindings() {
		return config.bindings;
	}

	public String getInputType() {
		return config.inputType;
	}

	public Map<Binding, Set<Binding>> cloneDependencies() {
		return config.dependencies.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, kv -> new HashSet<>(kv.getValue())));
	}

}
