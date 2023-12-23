package ch.scaille.tcwriter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Metadata {

	private String transientId = "";

	private String description = "";

	public Metadata() {
	}

	public Metadata(String transientId, String description) {
		this.transientId = transientId;
		this.description = description;
	}

	@JsonIgnore
	public String getTransientId() {
		return transientId;
	}

	public void setTransientId(String transientId) {
		this.transientId = transientId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return transientId;
	}
	
}
