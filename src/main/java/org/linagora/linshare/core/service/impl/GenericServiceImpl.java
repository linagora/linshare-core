package org.linagora.linshare.core.service.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericServiceImpl<R, E> {

	protected static Logger logger = LoggerFactory
			.getLogger(GenericServiceImpl.class);

	protected final AbstractResourceAccessControl<Account, R, E> rac;

	public GenericServiceImpl(AbstractResourceAccessControl<Account, R, E> rac) {
		super();
		this.rac = rac;
	}

	protected void preChecks(Account actor, Account owner) {
		preChecks(actor, owner, false);
	}

	protected void preChecks(Account actor, Account owner, boolean dontCheckOwner) {
		Validate.notNull(actor, "Missing actor account");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		if (!dontCheckOwner) {
			Validate.notNull(owner, "Missing owner account");
			Validate.notEmpty(owner.getLsUuid(), "Missing owner uuid");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Current actor " + actor.getAccountReprentation());
			if (!dontCheckOwner) {
				logger.debug("Current owner " + owner.getAccountReprentation());
			}
		}
	}

	protected void checkReadPermission(Account actor, E entry,
			BusinessErrorCode errCode) {
		rac.checkReadPermission(actor, entry, errCode);
	}

	protected void checkListPermission(Account actor, Account owner,
			EntryType type, BusinessErrorCode errCode) throws BusinessException {
		rac.checkListPermission(actor, owner, type, errCode);
	}

	protected void checkCreatePermission(Account actor, Account owner,
			EntryType type, BusinessErrorCode errCode) throws BusinessException {
		rac.checkCreatePermission(actor, owner, type, errCode);
	}

	protected void checkUpdatePermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		rac.checkUpdatePermission(actor, entry, errCode);
	}

	protected void checkDeletePermission(Account actor, E entry,
			BusinessErrorCode errCode) throws BusinessException {
		rac.checkDeletePermission(actor, entry, errCode);
	}
}
