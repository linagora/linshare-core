package org.linagora.linshare.core.service;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareEntryService {
	
	public ShareEntry findByUuid(User actor, String uuid)  throws BusinessException ;

	public ShareEntry createShare(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate) throws BusinessException;

	public SuccessesAndFailsItems<ShareEntry> createShare(DocumentEntry documentEntry, User sender, List<User> recipients, Calendar expirationDate) ;
	
	public SuccessesAndFailsItems<ShareEntry> createShare(List<DocumentEntry> documentEntries, User sender, List<User> recipients, Calendar expirationDate);

	public void deleteShare(String shareUuid, User actor, MailContainer mailContainer) throws BusinessException;
	
	public void deleteShare(ShareEntry share, User actor, MailContainer mailContainer) throws BusinessException;

	public DocumentEntry copyDocumentFromShare(String shareUuid, User actor) throws BusinessException; 

	public void updateShareComment(User actor, String uuid, String comment) throws BusinessException;
	
	public boolean shareHasThumbnail(User actor, String shareEntryUuid);
	
	public InputStream getShareThumbnailStream(User actor, String shareEntryUuid) throws BusinessException;
	
	public InputStream getShareStream(User actor, String shareEntryUuid) throws BusinessException;
	
	public void sendDocumentEntryUpdateNotification(Account actor, ShareEntry shareEntry, String friendlySize, String originalFileName, MailContainer mailContainer);
	
}
