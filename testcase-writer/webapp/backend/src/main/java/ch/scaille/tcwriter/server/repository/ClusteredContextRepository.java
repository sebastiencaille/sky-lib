package ch.scaille.tcwriter.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ch.scaille.tcwriter.server.model.ClusteredContext;

@Repository
public interface ClusteredContextRepository extends JpaRepository<ClusteredContext, String> {

	@Modifying
	@Query("UPDATE ClusteredContext c SET c.context = :context, c.lastAccess = :lastAccess  WHERE c.sessionId = :sessionId")
	void update(@Param("sessionId") String sessionId, @Param("context") String context, @Param("lastAccess") long currentTimeMillis);

	@Modifying
	@Query("UPDATE ClusteredContext c SET c.lastAccess = :lastAccess  WHERE c.sessionId = :sessionId")
	void touch(@Param("sessionId") String sessionId, @Param("lastAccess") long lastAccess);
	
	@Modifying
	@Query("DELETE ClusteredContext c WHERE c.lastAccess < :deleteBefore")
	void deleteAllBefore(@Param("deleteBefore") long oldestLastAccessToKeep);

	@Query("SELECT c FROM ClusteredContext c WHERE c.sessionId = :sessionId")
	ClusteredContext getBySessionId(@Param("sessionId")String sessionId);

	
}
