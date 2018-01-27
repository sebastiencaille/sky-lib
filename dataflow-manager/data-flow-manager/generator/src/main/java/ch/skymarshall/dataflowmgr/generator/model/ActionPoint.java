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
package ch.skymarshall.dataflowmgr.generator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionPoint {
	public UUID uuid;
	public String name;
	public String description;
	public String activator;
	public String input;
	public String output;
	public boolean terminal;

	@JsonProperty("broadcast-groups")
	public String[] broadcastGroups;
	@JsonProperty("input-rules")
	public List<InFlowRule> inputRules = new ArrayList<>();
	@JsonProperty("output-rules")
	public List<OutFlowRule> outputRules = new ArrayList<>();

	@JsonIgnore
	public Action action;

	@Override
	public String toString() {
		return name;
	}
}
