package org.linagora.linshare.core.facade.webservice.delegation.impl;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.delegation.DocumentFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.webservice.delegation.dto.DocumentDto;

import com.google.common.collect.Lists;

public class DocumentFacadeImpl extends DelegationGenericFacadeImpl implements
		DocumentFacade {

	private final DocumentEntryService documentEntryService;

	public DocumentFacadeImpl(AccountService accountService,
			DocumentEntryService documentEntryService) {
		super(accountService);
		this.documentEntryService = documentEntryService;
	}

	@Override
	public List<DocumentDto> getAll(String ownerUuid) throws BusinessException {
		User actor = checkAuthentication();
		Account owner = accountService.findByLsUuid(ownerUuid);
		if (owner == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "error");
		}
		List<DocumentEntry> list = documentEntryService.findAll(actor, owner);
		return Lists.transform(list, DocumentDto.toDelegationVo());
	}

}
