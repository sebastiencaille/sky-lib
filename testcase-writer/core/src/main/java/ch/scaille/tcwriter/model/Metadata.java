package ch.scaille.tcwriter.model;

public class Metadata {

	private transient String transientId = "";

	private String description = "";

	public Metadata() {
	}

	public Metadata(String transientId, String description) {
		this.transientId = transientId;
		this.description = description;
	}

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
