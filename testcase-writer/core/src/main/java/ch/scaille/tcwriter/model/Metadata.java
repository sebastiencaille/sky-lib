package ch.scaille.tcwriter.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
public class Metadata {

	private String transientId = "";

	@Getter
    private String description = "";
	
	@Getter
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

    @Override
	public String toString() {
		return transientId;
	}
	
}
