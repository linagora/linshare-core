package org.linagora.linshare.core.service;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareEntryService {
	
	public ShareEntry findById(User actor, String uuid)  throws BusinessException ;

	
	public ShareEntry createShare(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate) throws BusinessException;
	

	public SuccessesAndFailsItems<ShareEntry> createShare(DocumentEntry documentEntry, User sender, List<User> recipients, Calendar expirationDate) ;

	
	public SuccessesAndFailsItems<ShareEntry> createShare(List<DocumentEntry> documentEntries, User sender, List<User> recipients, Calendar expirationDate);
		

	public void deleteShare(String shareUuid, User actor) throws BusinessException;

	public DocumentEntry copyDocumentFromShare(String shareUuid, User actor) throws BusinessException; 
	
	public void deleteAllShareEntriesWithDocumentEntries(String docEntryUuid, User actor);
	  

	public void updateShareComment(User actor, String uuid, String comment) throws BusinessException;
	
}
