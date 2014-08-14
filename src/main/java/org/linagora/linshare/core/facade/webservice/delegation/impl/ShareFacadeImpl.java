package org.linagora.linshare.core.facade.webservice.delegation.impl;

import org.linagora.linshare.core.facade.webservice.delegation.ShareFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;

public class ShareFacadeImpl extends DelegationGenericFacadeImpl implements ShareFacade {

	private final ShareEntryService shareEntryService;

	private final ShareService shareService;

	public ShareFacadeImpl(AccountService accountService,
			ShareEntryService shareEntryService, ShareService shareService) {
		super(accountService);
		this.shareEntryService = shareEntryService;
		this.shareService = shareService;
	}


}
