package ch.skymarshall.dataflowmgr.generator.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OutFlowRule {

	public UUID uuid;
	public String output;
	public String activator;
	@JsonProperty("transform-function")
	public String transformFunction;
	public String nextAction;
}
