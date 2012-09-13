package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;

public interface AnonymousShareEntryRepository extends AbstractRepository<AnonymousShareEntry> {

	 /** Find a anonymous share using its uuid.
     * @param  uuid
     * @return found share (null if not found).
     */
	public AnonymousShareEntry findById(String uuid);
	
	public List<AnonymousShareEntry> findAllExpiredEntries();

	
	public List<AnonymousShareEntry> findUpcomingExpiredEntries(Integer date);
}
