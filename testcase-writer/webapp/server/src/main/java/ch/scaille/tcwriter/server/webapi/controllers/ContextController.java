package ch.scaille.tcwriter.server.webapi.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.scaille.tcwriter.server.dao.ContextDao;
import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.tcwriter.server.services.ContextService;
import ch.scaille.util.helpers.Logs;

@RequestMapping("/context")
public class ContextController {

	private final ContextService contextService;
	
	private final ContextDao contextDao;

	public ContextController(ContextService contextService, ContextDao contextDao) {
		this.contextService = contextService;
		this.contextDao = contextDao;
	}

	@GetMapping(path = "", produces = "application/json")
	@ResponseBody
	public Context get() {
		return contextService.get();
	}

	@PutMapping(path = "", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Context set(HttpServletRequest request, @RequestBody Context newContext) {
		Context context = contextDao.save(Identity.of(request), contextService.merge(newContext));
		Logs.of(getClass()).info(() -> "Setting context: " + context);
		return context;
	}

}
