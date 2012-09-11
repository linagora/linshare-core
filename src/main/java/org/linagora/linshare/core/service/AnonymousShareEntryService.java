package org.linagora.linshare.core.service;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface AnonymousShareEntryService {

	public AnonymousShareEntry findByUuid(Account actor, String shareUuid)  throws BusinessException ;

	public List<AnonymousShareEntry> createAnonymousShare(List<DocumentEntry> documentEntries, User sender, Contact recipient, Calendar expirationDate, Boolean passwordProtected, MailContainer mailContainer) throws BusinessException;
	
	public List<AnonymousShareEntry> createAnonymousShare(List<DocumentEntry> documentEntries, User sender, List<Contact> recipients, Calendar expirationDate, Boolean passwordProtected, MailContainer mailContainer) throws BusinessException;

	public void deleteShare(Account actor, String shareUuid) throws BusinessException;
	
	public void deleteShare(Account actor, AnonymousShareEntry share) throws BusinessException;
	
	public void deleteShare(SystemAccount systemAccount, AnonymousShareEntry share) throws BusinessException;
	
	public InputStream getAnonymousShareEntryStream(String shareUuid, MailContainer mailContainer) throws BusinessException ;
	
	public void sendDocumentEntryUpdateNotification(AnonymousShareEntry anonymousShareEntry, String friendlySize, String originalFileName);

	public void sendUpcomingOutdatedShareEntryNotification(SystemAccount actor, AnonymousShareEntry shareEntry, Integer days);
}
