package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface EntryService {

	/**
	 * The document entry and all its shares will be removed. A mail notification will be sent.
	 * @param actor
	 * @param docEntryUuid
	 * @param mailContainer
	 * @throws BusinessException
	 */
	public void deleteAllShareEntriesWithDocumentEntry(Account actor, String docEntryUuid, MailContainer mailContainer) throws BusinessException;
	
	public void deleteAllInconsistentShareEntries(Account actor, DocumentEntry documentEntry) throws BusinessException;
	
	/**
	 * The document entry and all its shares will be removed. No mail will be sent.
	 * @param docEntryUuid
	 * @param actor
	 * @throws BusinessException
	 */
	public void deleteAllShareEntriesWithDocumentEntry(Account actor, String docEntryUuid) throws BusinessException;
	
	/**
	 * All The document entries own by the user "owner" and all its shares will be removed. No mail will be sent.
	 * @param owner
	 * @return
	 */
	public void deleteAllShareEntriesWithDocumentEntries(Account actor, User owner ) throws BusinessException;
	
	/**
	 * All The share entries received by the user "recipient" will be removed. No mail will be sent.
	 * @param actor
	 * @param owner
	 * @throws BusinessException
	 */
	public void deleteAllReceivedShareEntries(Account actor, User recipient ) throws BusinessException;
	
	
	
	
	
	public void sendSharedUpdateDocNotification(Account actor, DocumentEntry documentEntry, String friendlySize, String originalFileName, MailContainer mailContainer);
	
	

}
