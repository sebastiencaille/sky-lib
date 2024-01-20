package ch.scaille.tcwriter.server.webapi.v0.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.v0.ContextApiController;
import ch.scaille.tcwriter.generated.api.model.v0.Context;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.services.SessionAccessor;
import ch.scaille.tcwriter.server.webapi.v0.mappers.ContextMapper;
import ch.scaille.util.helpers.Logs;
import jakarta.validation.Valid;

public class ContextController extends ContextApiController {

	private final SessionAccessor sessionAccessor;
	private final ContextFacade contextService;

	public ContextController(SessionAccessor sessionAccessor, ContextFacade contextService,
			NativeWebRequest webNativeRequest) {
		super(webNativeRequest);
		this.sessionAccessor = sessionAccessor;
		this.contextService = contextService;
	}

	@Override
	public ResponseEntity<Context> getCurrent() {
		final var context = sessionAccessor.getContext(getRequest()).mandatory();
		return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
	}

	@Override
	public ResponseEntity<Context> setCurrent(@Valid Context contextUpdate) {
		final var sessionContext = sessionAccessor.getContext(getRequest());
		final var context = sessionContext.mandatory();
		contextService.merge(context, ContextMapper.MAPPER.convert(contextUpdate));
		Logs.of(getClass()).info(() -> "Setting new context: " + context);
		sessionContext.set(context);
		return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
	}

}
