package org.linagora.linshare.core.business.service.impl;

import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.service.impl.ShareExpiryDateServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.linagora.linshare.core.utils.HashUtils;

public class AnonymousUrlBusinessServiceImpl implements AnonymousUrlBusinessService {

	private final AnonymousUrlRepository anonymousUrlRepository;
	private final String baseSecuredUrl;
	private final PasswordService passwordService;
	
	private static final Logger logger = LoggerFactory.getLogger(AnonymousUrlBusinessServiceImpl.class);
	
	
	public AnonymousUrlBusinessServiceImpl(AnonymousUrlRepository anonymousUrlRepository, String baseSecuredUrl, PasswordService passwordService) {
		super();
		this.anonymousUrlRepository = anonymousUrlRepository;
		this.baseSecuredUrl = baseSecuredUrl;
		this.passwordService = passwordService;
	}


	@Override
	public AnonymousUrl findByUuid(String uuid) {
		return anonymousUrlRepository.findByUuid(uuid);
	}

	
	@Override
	public AnonymousUrl create(Boolean passwordProtected) throws BusinessException {
		
		AnonymousUrl anonymousUrl = new AnonymousUrl(baseSecuredUrl);
		if(passwordProtected) {
			String password = passwordService.generatePassword();
			// We store it temporay it this object for mail notification.
			anonymousUrl.setTemporaryPlainTextPassword(password);
			anonymousUrl.setPassword(HashUtils.hashSha1withBase64(password.getBytes()));
		}
		return anonymousUrlRepository.create(anonymousUrl);
	}


	@Override
	public void update(AnonymousUrl anonymousUrl) throws BusinessException {
		anonymousUrlRepository.update(anonymousUrl);
	}
	
}
