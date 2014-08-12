package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessException;

public interface ShareService {

	public void create(Account actor, User owner, ShareContainer shareContainer)
			throws BusinessException;

	public DocumentEntry deleteAllShareEntries(Account actor, Account owner,
			String docEntryUuid) throws BusinessException;
}
