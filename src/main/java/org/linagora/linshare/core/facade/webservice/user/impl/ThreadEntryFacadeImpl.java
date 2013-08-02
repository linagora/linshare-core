package org.linagora.linshare.core.facade.webservice.user.impl;

import java.io.InputStream;

import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.ThreadEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.webservice.dto.ThreadEntryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadEntryFacadeImpl extends GenericFacadeImpl implements ThreadEntryFacade {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ThreadFacadeImpl.class);
	
	private final ThreadEntryService threadEntryService;
	private final ThreadService threadService;
	private final FunctionalityService functionalityService;

	public ThreadEntryFacadeImpl(AccountService accountService, ThreadService threadService, ThreadEntryService threadEntryService, FunctionalityService functionalityService) {
		super(accountService);
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.functionalityService = functionalityService;
	}
	
	@Override
	public User checkAuthentication() throws BusinessException {
		User user = super.checkAuthentication();
		Functionality functionality = functionalityService.getThreadTabFunctionality(user.getDomain());

		if (!functionality.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		return user;
	}

	@Override
	public ThreadEntryDto uploadfile(String threadUuid, InputStream fi, String filename, String description) throws BusinessException {
		User actor = getAuthentication();
		Thread thread = threadService.findByLsUuid(threadUuid);
		if (thread == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT, "Current thread was not found : " + threadUuid);
		}
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor, thread, fi, filename);
		threadEntryService.updateFileProperties(actor, threadEntry.getUuid(), description);
		return new ThreadEntryDto(threadEntry);
	}

}
