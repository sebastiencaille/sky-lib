package ch.scaille.tcwriter.server.webapi.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.ContextApiController;
import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.tcwriter.server.webapi.mappers.ContextMapper;
import ch.scaille.util.helpers.Logs;

public class ContextController extends ContextApiController {

	private final ContextService contextService;

	private final ContextDao contextDao;

	public ContextController(ContextService contextService, ContextDao contextDao, NativeWebRequest webNativeRequest) {
		super(webNativeRequest);
		this.contextService = contextService;
		this.contextDao = contextDao;
	}

	@Override
	public ResponseEntity<ch.scaille.tcwriter.generated.api.model.Context> getCurrent() {
		return new ResponseEntity<>(ContextMapper.MAPPER.convert(contextService.get()), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ch.scaille.tcwriter.generated.api.model.Context> setCurrent(
			ch.scaille.tcwriter.generated.api.model.@Valid ContextUpdate contextUpdate) {
		final var modelContext = contextDao.save(
				Identity.of(getRequest().orElseThrow(() -> new IllegalStateException("No request available"))
						.getNativeRequest(HttpServletRequest.class)),
				contextService.merge(ContextMapper.MAPPER.convert(contextUpdate)));
		Logs.of(getClass()).info(() -> "Setting new context: " + modelContext);
		return new ResponseEntity<>(ContextMapper.MAPPER.convert(modelContext), HttpStatus.OK);
	}

}
