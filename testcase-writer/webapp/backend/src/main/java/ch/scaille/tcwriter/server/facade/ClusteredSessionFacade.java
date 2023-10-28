package ch.scaille.tcwriter.server.facade;

import java.util.function.LongPredicate;

import ch.scaille.tcwriter.server.dto.Context;

public interface ClusteredSessionFacade {

	Context loadAndValidate(String sessionId, LongPredicate isExpired);

	void save(String sessionId, Context appContext);

	void update(String sessionId, Context appContext);

	void touch(String sessionId);

	void deleteExpiredSessions(long delay);

}
