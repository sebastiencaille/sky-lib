package ch.scaille.tcwriter.server.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.dto.Identity;

public class InMemoryContextDao implements ContextDao {

	private final Map<Identity, Context> contexts = new ConcurrentHashMap<>();

	@Override
	public Context loadContext(Identity identity) {
		return contexts.computeIfAbsent(identity, Context::new);
	}

	@Override
	public Context save(Identity identity, Context context) {
		contexts.put(identity, context);
		return context;
	}

}
