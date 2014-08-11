package org.linagora.linshare.core.rac;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessException;

public interface DocumentEntryResourceAccessControl extends
		AbstractResourceAccessControl<Account, DocumentEntry> {

	void checkDownloadPermission(Account actor, DocumentEntry entry)
			throws BusinessException;

	void checkThumbNailDownloadPermission(Account actor, DocumentEntry entry)
			throws BusinessException;

}
