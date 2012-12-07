package org.linagora.linshare.core.business.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public AnonymousUrl create(Boolean passwordProtected, Contact contact) throws BusinessException {
		
		AnonymousUrl anonymousUrl = new AnonymousUrl(baseSecuredUrl, contact);
		if(passwordProtected) {
			String password = passwordService.generatePassword();
			// We store it temporary in this object for mail notification.
			anonymousUrl.setTemporaryPlainTextPassword(password);
			anonymousUrl.setPassword(HashUtils.hashSha1withBase64(password.getBytes()));
		}
		return anonymousUrlRepository.create(anonymousUrl);
	}


	@Override
	public void update(AnonymousUrl anonymousUrl) throws BusinessException {
		anonymousUrlRepository.update(anonymousUrl);
	}


	@Override
	public AnonymousUrl getAnonymousUrl(String uuid) throws LinShareNotSuchElementException {
		AnonymousUrl anonymousUrl = findByUuid(uuid);
		if(anonymousUrl == null) {
			 throw new LinShareNotSuchElementException("anonymousUrl url not found");
		}
		return anonymousUrl;
	}


	@Override
	public boolean isValidPassword(AnonymousUrl anonymousUrl, String password) {
		if (anonymousUrl == null) throw new IllegalArgumentException("anonymousUrl url cannot be null");

		// Check password validity
		if (password != null) {
			String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
			return hashedPassword.equals(anonymousUrl.getPassword());
		}
		return true;
	}


	@Override
	public boolean isExpired(AnonymousUrl anonymousUrl) {
		if (anonymousUrl == null)
			throw new IllegalArgumentException("anonymousUrl url cannot be null");

		Set<AnonymousShareEntry> entries = anonymousUrl.getAnonymousShareEntries();
		
		if(entries != null && entries.size() > 0) {
			return false;
		}
		return true;
	}

	
}
