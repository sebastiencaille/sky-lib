package ch.skymarshall.dataflowmgr.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ActionPoint {
	public String name;
	public String description;
	public String input;
	public String output;
	public boolean terminal;
	@JsonIgnore
	public Action action;
}
