package org.linagora.linshare.core.rac;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

public interface EntryResourceAccessControl<R, E extends Entry> extends
		AbstractResourceAccessControl<Account, R, E> {

	void checkDownloadPermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException;

	void checkThumbNailDownloadPermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException;
}
