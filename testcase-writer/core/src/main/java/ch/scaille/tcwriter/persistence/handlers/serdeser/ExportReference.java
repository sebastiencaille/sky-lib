package ch.scaille.tcwriter.persistence.handlers.serdeser;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ExportReference<M, T> (@JsonIgnore T object, String ref, @JsonIgnore ReferenceHandler<M, T> handler) {

	public void apply(M model) {
		handler.importer().apply(model, object, ref);
	}
}
