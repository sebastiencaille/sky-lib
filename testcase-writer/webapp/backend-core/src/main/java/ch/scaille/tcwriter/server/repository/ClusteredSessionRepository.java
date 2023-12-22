package ch.scaille.tcwriter.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ch.scaille.tcwriter.server.model.ClusteredSession;

@Repository
public interface ClusteredSessionRepository extends JpaRepository<ClusteredSession, String> {

	@Modifying
	@Query("UPDATE ClusteredSession c SET c.context = :context, c.lastAccess = :lastAccess  WHERE c.sessionId = :sessionId")
	void update(@Param("sessionId") String sessionId, @Param("context") String context, @Param("lastAccess") long currentTimeMillis);

	@Modifying
	@Query("UPDATE ClusteredSession c SET c.lastAccess = :lastAccess  WHERE c.sessionId = :sessionId")
	void touch(@Param("sessionId") String sessionId, @Param("lastAccess") long lastAccess);

	@Modifying
	@Query("DELETE FROM ClusteredSession c WHERE c.sessionId = :sessionId")
	void delete(@Param("sessionId")String sessionId);
	
	@Modifying
	@Query("DELETE ClusteredSession c WHERE c.lastAccess < :deleteBefore")
	void deleteAllBefore(@Param("deleteBefore") long oldestLastAccessToKeep);

	@Query("SELECT c FROM ClusteredSession c WHERE c.sessionId = :sessionId")
	ClusteredSession getBySessionId(@Param("sessionId")String sessionId);

	
}
