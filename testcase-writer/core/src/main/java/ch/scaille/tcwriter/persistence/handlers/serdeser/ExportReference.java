package ch.scaille.tcwriter.persistence.handlers.serdeser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.java.Log;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

@Log
public record ExportReference<M, T> (@JsonIgnore @Nullable T object, @Nullable String ref, @JsonIgnore @Nullable ReferenceHandler<M, T> handler) implements Consumer<M> {

	@Override
	public void accept(M model) {
		if (object == null || ref == null) {
			log.warning("Reference cannot be resolved because either object or ref is null");
			return;
		}
		Objects.requireNonNull(handler, "No handler set").importer().apply(model, object, ref);
	}
}
