package ch.scaille.tcwriter.server.services;

public class AbstractContextualService {
	protected final ContextService contextService;
	
	public AbstractContextualService(ContextService contextService) {
		this.contextService = contextService;
	}
}
