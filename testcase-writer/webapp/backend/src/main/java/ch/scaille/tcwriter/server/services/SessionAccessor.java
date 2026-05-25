package ch.scaille.tcwriter.server.services;

import java.util.Optional;

public interface SessionAccessor {
	<T> Optional<T> get(String attribName);

	void set(String attribName, Object value);

	void remove(String attribName);
}
