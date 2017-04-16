package ch.skymarshall.dataflowmgr.generator.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InFlowRule {

	public UUID uuid;
	public String input;
	public String activator;
	@JsonProperty("transform-function")
	public String transformFunction;
	@JsonProperty("transform-consumer")
	public String transformConsumer;

}
