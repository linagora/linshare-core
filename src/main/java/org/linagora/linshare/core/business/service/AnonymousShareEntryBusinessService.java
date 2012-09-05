package org.linagora.linshare.core.business.service;

import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

public interface AnonymousShareEntryBusinessService {
	
	public AnonymousShareEntry findByUuid(String uuid);

	public AnonymousUrl createAnonymousShare(List<DocumentEntry> documentEntries, User sender, Contact recipient, Calendar expirationDate, Boolean passwordProtected) throws BusinessException;
	
	public void deleteAnonymousShare(AnonymousShareEntry anonymousShare) throws BusinessException;

}
