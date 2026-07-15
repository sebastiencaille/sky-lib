package ch.scaille.tcwriter.server.webapi.v0.controllers;

import ch.scaille.tcwriter.generated.api.controllers.v0.ContextApi;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.v0.ContextApiController;
import ch.scaille.tcwriter.generated.api.model.v0.Context;
import ch.scaille.tcwriter.server.services.SessionManager;
import ch.scaille.tcwriter.server.webapi.v0.mappers.ContextMapper;
import lombok.extern.java.Log;

@Log
public class ContextController extends ContextApiController {

	private final SessionManager sessionAccessor;

	public ContextController(SessionManager sessionAccessor,
			NativeWebRequest webNativeRequest) {
		super(webNativeRequest);
		this.sessionAccessor = sessionAccessor;
	}

	@PostMapping(value = ContextApi.PATH_GET_CURRENT)
	@Transactional
	public ResponseEntity<Void> init() {
		validateAndRememberCurrent(new Context());
		return ResponseEntity.ok(null);
	}

	@Transactional(readOnly = true)
	@Override
	public ResponseEntity<Context> getCurrent() {
		final var context = sessionAccessor.getContext(getRequest().orElseThrow()).mandatory();
		return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
	}

	@Override
	@Transactional
	public ResponseEntity<Context> validateAndRememberCurrent(Context context) {
		validateAndSet(context);
		return ResponseEntity.ok(context);
	}

	private void validateAndSet(Context context) {
		final var sessionContext = sessionAccessor.getContext(getRequest().orElseThrow());
		log.info(() -> "Remember context: " + context);
		sessionContext.set(ContextMapper.MAPPER.convert(context));
	}

}
