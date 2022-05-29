package ch.scaille.tcwriter.server.dao;

import ch.scaille.tcwriter.server.dto.Context;
import ch.scaille.tcwriter.server.dto.Identity;

public interface ContextDao {

	Context loadContext(Identity identity) ;

	Context save(Identity identity, Context context);

}
