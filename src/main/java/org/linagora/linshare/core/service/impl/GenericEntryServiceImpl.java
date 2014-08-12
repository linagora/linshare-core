package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.EntryResourceAccessControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericEntryServiceImpl<T extends Entry> extends
		GenericServiceImpl<T> {

	protected static Logger logger = LoggerFactory
			.getLogger(GenericEntryServiceImpl.class);

	protected final EntryResourceAccessControl<T> rac;

	public GenericEntryServiceImpl(EntryResourceAccessControl<T> rac) {
		super(rac);
		this.rac = rac;
	}

	protected void checkDownloadPermission(Account actor, T entry,
			BusinessErrorCode errCode) throws BusinessException {
		rac.checkDownloadPermission(actor, entry, errCode);
	}

	protected void checkThumbNailDownloadPermission(Account actor, T entry,
			BusinessErrorCode errCode) throws BusinessException {
		rac.checkThumbNailDownloadPermission(actor, entry, errCode);
	}
}
