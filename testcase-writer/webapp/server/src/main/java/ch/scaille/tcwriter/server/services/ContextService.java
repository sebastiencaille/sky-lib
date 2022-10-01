package ch.scaille.tcwriter.server.services;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.dto.Identity;
import ch.scaille.util.helpers.JavaExt.AutoCloseableNoException;

public interface ContextService {
	
	AutoCloseableNoException set(Context context);
	
	Context get();

	Context merge(Context newContext);

	AutoCloseableNoException load(Identity of);
	
}
