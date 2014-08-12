package org.linagora.linshare.core.service.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.AbstractResourceAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericServiceImpl<T> {

	protected static Logger logger = LoggerFactory
			.getLogger(GenericServiceImpl.class);

	protected final AbstractResourceAccessControl<Account, T> rac;

	public GenericServiceImpl(AbstractResourceAccessControl<Account, T> rac) {
		super();
		this.rac = rac;
	}

	protected void preChecks(Account actor, Account owner) {
		Validate.notNull(actor, "Missing actor account");
		Validate.notEmpty(actor.getLsUuid(), "Missing actor uuid");
		Validate.notNull(owner, "Missing owner account");
		Validate.notEmpty(owner.getLsUuid(), "Missing owner uuid");
		if (logger.isDebugEnabled()) {
			logger.debug("Current actor " + actor.getAccountReprentation());
			logger.debug("Current owner " + actor.getAccountReprentation());
		}
	}

	protected void checkReadPermission(Account actor, T entry,
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

	protected void checkUpdatePermission(Account actor, T entry,
			BusinessErrorCode errCode) throws BusinessException {
		rac.checkUpdatePermission(actor, entry, errCode);
	}

	protected void checkDeletePermission(Account actor, T entry,
			BusinessErrorCode errCode) throws BusinessException {
		rac.checkDeletePermission(actor, entry, errCode);
	}
}
