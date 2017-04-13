package ch.skymarshall.dataflowmgr.generator.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Module {
	public String name;

	@JsonProperty(value = "package-name")
	public String packageName;

	public List<Dto> dtos;
	public List<ActionPoint> actions;
	public List<Flow> flows;

}
