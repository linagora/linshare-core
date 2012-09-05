package org.linagora.linshare.core.business.service;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareEntryBusinessService {

	public ShareEntry createShare(DocumentEntry documentEntry, User sender, User recipient, Calendar expirationDate) throws BusinessException;
	
	public void deleteShare(ShareEntry share) throws BusinessException;
	
	public ShareEntry findByUuid(String uuid);

	public void updateShareComment(ShareEntry share, String comment) throws BusinessException;
	
	public List<ShareEntry> findAllMyShareEntries(User owner);
	
}
