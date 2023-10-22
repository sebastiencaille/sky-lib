package ch.scaille.tcwriter.server.facade;

import ch.scaille.tcwriter.server.dto.Context;

public interface ContextFacade {
	
	Context merge(Context current, Context newContext);

}
