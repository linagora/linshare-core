package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;

public interface AnonymousShareEntryRepository extends AbstractRepository<AnonymousShareEntry> {

	 /** Find a anonymous share using its uuid.
     * @param  uuid
     * @return found share (null if not found).
     */
	public AnonymousShareEntry findById(String uuid);
	
	/**
	 * 
	 * @param documentEntry
	 * @param sender : user entity
	 * @param recipient : Contact object
	 * @return
	 */
	public AnonymousShareEntry getAnonymousShareEntry(DocumentEntry documentEntry, User sender, Contact recipient);
	
}
