package org.linagora.linshare.core.rac;

import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

public interface AbstractResourceAccessControl<O, E> {

	void checkReadPermission(Account actor, E entry, BusinessErrorCode errCode)
			throws BusinessException;

	void checkListPermission(Account actor, O owner, EntryType type,
			BusinessErrorCode errCode) throws BusinessException;

	void checkCreatePermission(Account actor, O owner, EntryType type,
			BusinessErrorCode errCode) throws BusinessException;

	void checkUpdatePermission(Account actor, E entry, BusinessErrorCode errCode)
			throws BusinessException;

	void checkDeletePermission(Account actor, E entry, BusinessErrorCode errCode)
			throws BusinessException;
}
