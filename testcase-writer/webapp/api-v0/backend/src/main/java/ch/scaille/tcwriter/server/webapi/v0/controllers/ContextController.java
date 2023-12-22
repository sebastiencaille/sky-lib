package ch.scaille.tcwriter.server.webapi.v0.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.v0.ContextApiController;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.webapi.v0.mappers.ContextMapper;
import ch.scaille.tcwriter.generated.api.model.v0.Context;
import ch.scaille.util.helpers.Logs;
import jakarta.validation.Valid;

public class ContextController extends ContextApiController {

	private final ContextFacade contextService;

	private final ch.scaille.tcwriter.server.dto.Context context;

	public ContextController(ch.scaille.tcwriter.server.dto.Context context, ContextFacade contextService,
			NativeWebRequest webNativeRequest) {
		super(webNativeRequest);
		this.context = context;
		this.contextService = contextService;
	}

	@Override
	public ResponseEntity<Context> getCurrent() {
		return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
	}

	@Override
	public ResponseEntity<Context> setCurrent(@Valid Context contextUpdate) {
		contextService.merge(context, ContextMapper.MAPPER.convert(contextUpdate));
		Logs.of(getClass()).info(() -> "Setting new context: " + context);
		return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
	}

}
