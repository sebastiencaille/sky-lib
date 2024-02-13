package ch.scaille.tcwriter.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Metadata {

	private String transientId = "";

	private String description = "";
	
	private LocalDateTime creationDate; 

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

	public LocalDateTime getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}
	
	@Override
	public String toString() {
		return transientId;
	}
	
}
