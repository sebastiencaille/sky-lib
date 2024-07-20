package ch.scaille.tcwriter.server.webapi.v0.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.v0.ContextApiController;
import ch.scaille.tcwriter.generated.api.model.v0.Context;
import ch.scaille.tcwriter.server.services.SessionAccessor;
import ch.scaille.tcwriter.server.webapi.v0.mappers.ContextMapper;
import ch.scaille.util.helpers.Logs;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ContextController extends ContextApiController {

	private final SessionAccessor sessionAccessor;

	public ContextController(SessionAccessor sessionAccessor,
			NativeWebRequest webNativeRequest) {
		super(webNativeRequest);
		this.sessionAccessor = sessionAccessor;
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<Context> getCurrent() {
		final var context = sessionAccessor.getContext(getRequest().orElse(null)).mandatory();
		return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
	}

	@Override
	@Transactional
	public ResponseEntity<Context> validateAndRememberCurrent(@Valid @NotNull Context context) {
		final var sessionContext = sessionAccessor.getContext(getRequest().orElse(null));
		Logs.of(getClass()).info(() -> "Remember context: " + context);
		sessionContext.set(ContextMapper.MAPPER.convert(context));
		return ResponseEntity.ok(context);
	}

}
