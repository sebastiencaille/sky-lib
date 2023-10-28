package ch.scaille.tcwriter.server.facade;

import ch.scaille.tcwriter.server.dto.Context;

public interface ContextFacade {
	
	void merge(Context current, Context newContext);

}
