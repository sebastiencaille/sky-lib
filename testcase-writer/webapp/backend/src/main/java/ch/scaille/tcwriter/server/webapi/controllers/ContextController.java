package ch.scaille.tcwriter.server.webapi.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import ch.scaille.tcwriter.generated.api.controllers.ContextApiController;
import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.facade.ContextFacade;
import ch.scaille.tcwriter.server.webapi.mappers.ContextMapper;
import ch.scaille.util.helpers.Logs;
import jakarta.validation.Valid;

public class ContextController extends ContextApiController {

    private final ContextFacade contextService;

    private Context context;
    
    public ContextController(Context context, ContextFacade contextService, NativeWebRequest webNativeRequest) {
        super(webNativeRequest);
        this.context = context;
        this.contextService = contextService;
    }

    @Override
    public ResponseEntity<ch.scaille.tcwriter.generated.api.model.Context> getCurrent() {
        return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
    }

    @Override
    public ResponseEntity<ch.scaille.tcwriter.generated.api.model.Context> setCurrent(
            ch.scaille.tcwriter.generated.api.model.@Valid Context contextUpdate) {
    	context = contextService.merge(context, ContextMapper.MAPPER.convert(contextUpdate));
        Logs.of(getClass()).info(() -> "Setting new context: " + context);
        return ResponseEntity.ok(ContextMapper.MAPPER.convert(context));
    }

}
