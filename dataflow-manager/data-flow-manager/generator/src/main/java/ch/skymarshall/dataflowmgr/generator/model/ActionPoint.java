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

	@JsonProperty("input-rules")
	public List<InFlowRule> inputRules = new ArrayList<>();
	@JsonProperty("output-rules")
	public List<OutFlowRule> outputRules = new ArrayList<>();

	@JsonIgnore
	public Action action;
}
