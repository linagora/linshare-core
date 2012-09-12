package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;

public interface AnonymousUrlBusinessService {
	
	public AnonymousUrl findByUuid(String uuid);
	
	public AnonymousUrl create(Boolean passwordProtected, Contact contact) throws BusinessException;
	
	public void update (AnonymousUrl anonymousUrl) throws BusinessException;
	
	public AnonymousUrl getAnonymousUrl(String uuid) throws LinShareNotSuchElementException;
	
	public boolean isValidPassword(AnonymousUrl anonymousUrl, String password);
	
	public boolean isExpired(AnonymousUrl anonymousUrl);
	
}
