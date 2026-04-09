package ch.scaille.tcwriter.persistence.handlers.serdeser;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.function.Consumer;

public record ExportReference<M, T> (@JsonIgnore T object, String ref, @JsonIgnore ReferenceHandler<M, T> handler) implements Consumer<M> {

	@Override
	public void accept(M model) {
		handler.importer().apply(model, object, ref);
	}
}
