package ch.scaille.tcwriter.server.webapi.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.ContextApiController;
import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.webapi.mappers.ContextMapper;
import ch.scaille.util.helpers.Logs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public class ContextController extends ContextApiController {

    private final ContextFacade contextService;

    private final ContextDao contextDao;

    public ContextController(ContextFacade contextService, ContextDao contextDao, NativeWebRequest webNativeRequest) {
        super(webNativeRequest);
        this.contextService = contextService;
        this.contextDao = contextDao;
    }

    @Override
    public ResponseEntity<ch.scaille.tcwriter.generated.api.model.Context> getCurrent() {
        return ResponseEntity.ok(ContextMapper.MAPPER.convert(contextService.get()));
    }

    @Override
    public ResponseEntity<ch.scaille.tcwriter.generated.api.model.Context> setCurrent(
            ch.scaille.tcwriter.generated.api.model.@Valid ContextUpdate contextUpdate) {
        final var modelContext = contextDao.save(
                Identity.of(getRequest().orElseThrow(() -> new IllegalStateException("No request available"))
                        .getNativeRequest(HttpServletRequest.class)),
                contextService.merge(ContextMapper.MAPPER.convert(contextUpdate)));
        Logs.of(getClass()).info(() -> "Setting new context: " + modelContext);
        return ResponseEntity.ok(ContextMapper.MAPPER.convert(modelContext));
    }

}
