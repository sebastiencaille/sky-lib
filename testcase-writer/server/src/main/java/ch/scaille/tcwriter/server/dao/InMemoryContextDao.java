package ch.scaille.tcwriter.server.dao;

import java.util.HashMap;
import java.util.Map;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.dto.Identity;

public class InMemoryContextDao implements ContextDao {

	private final Map<Identity, Context> contexts = new HashMap<>();
	
	@Override
	public Context loadContext(Identity identity) {
		return contexts.computeIfAbsent(identity, Context::new).derive();
	}

	@Override
	public Context save(Identity identity, Context context) {
		contexts.put(identity, context);
		return context;
	}

}
