package ch.scaille.tcwriter.server.facade;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.function.LongPredicate;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.model.ClusteredContext;
import ch.scaille.tcwriter.server.repository.ClusteredContextRepository;
import ch.scaille.util.helpers.Logs;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

public class ClusteredSessionFacadeImpl implements ClusteredSessionFacade {

	private static final java.util.logging.Logger LOGGER = Logs.of(ClusteredSessionFacadeImpl.class);

	@Autowired
	private ObjectMapper mapper;

	private final ClusteredContextRepository repository;

	public ClusteredSessionFacadeImpl(ClusteredContextRepository repository) {
		this.repository = repository;
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	@Override
	public Context loadAndValidate(String sessionId, LongPredicate isExpired) {
		LOGGER.info(() -> "load " + sessionId);
		final var context = repository.getBySessionId(sessionId);
		if (context == null) {
			return null;
		}
		final var owner = getOwner();
		if (!Objects.equal(owner, context.getOwner())) {
			// Security
			LOGGER.warning(() -> "Not restoring context because of owner mismatch: " + sessionId + ", expected " + owner
					+ " found " + context.getOwner());
			return null;
		}
		if (isExpired.test(context.getLastAccess())) {
			// Security
			LOGGER.info(() -> "Not restoring context because context is outdated");
			return null;
		} else {
			return deserialize(context);
		}
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	@Override
	public void save(String sessionId, Context appContext) {
		LOGGER.info(() -> "save " + sessionId);
		final var owner = getOwner();
		try {
			final var newContext = new ClusteredContext();
			newContext.setSessionId(sessionId);
			newContext.setOwner(owner);
			newContext.setContext(serialize(appContext));
			newContext.setLastAccess(System.currentTimeMillis());
			repository.save(newContext);
		} catch (JsonProcessingException e) {
			LOGGER.log(Level.WARNING, "Unable to save context", e);
		}
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	@Override
	public void update(String sessionId, Context appContext) {
		LOGGER.info(() -> "update " + sessionId);
		try {
			repository.update(sessionId, serialize(appContext), System.currentTimeMillis());
		} catch (JsonProcessingException e) {
			LOGGER.log(Level.WARNING, "Unable to update context", e);
		} catch (EntityNotFoundException e) {
			save(sessionId, appContext);
		}
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	@Override
	public void touch(String sessionId) {
		LOGGER.info(() -> "touch " + sessionId);
		repository.touch(sessionId, System.currentTimeMillis());
	}

	@Transactional(value = TxType.REQUIRES_NEW)
	@Override
	public void deleteExpiredSessions(long delay) {
		long deleteBefore = System.currentTimeMillis() - delay;
		LOGGER.info(() -> "deleteExpiredSessions before "
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(deleteBefore)));
		repository.deleteAllBefore(deleteBefore);
	}

	private String serialize(Context appContext) throws JsonProcessingException {
		return mapper.writeValueAsString(appContext.copy());
	}

	private Context deserialize(final ClusteredContext context) {
		try {
			return mapper.readValue(context.getContext(), Context.class);
		} catch (JsonProcessingException e) {
			LOGGER.log(Level.WARNING, "Unable to restore context", e);
			return null;
		}
	}

	private String getOwner() {
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return "ANON";
		}
		return authentication.getName();
	}

}
