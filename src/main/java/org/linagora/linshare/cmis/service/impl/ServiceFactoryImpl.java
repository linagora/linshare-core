package org.linagora.linshare.cmis.service.impl;

import org.apache.chemistry.opencmis.commons.impl.server.AbstractServiceFactory;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.CmisService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;

public class ServiceFactoryImpl extends AbstractServiceFactory {

	private final DocumentEntryService documentEntryService;

	private final AccountService accountService;

	private final CmisService cmisService;

	public ServiceFactoryImpl(DocumentEntryService documentEntryService,
			AccountService accountService, CmisService cmisService) {
		super();
		this.documentEntryService = documentEntryService;
		this.accountService = accountService;
		this.cmisService = cmisService;
	}

	@Override
	public CmisService getService(CallContext context) {
		return cmisService;
	}
}
