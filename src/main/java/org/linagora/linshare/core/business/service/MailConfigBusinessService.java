package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailConfigBusinessService {

	MailConfig findByUuid(String uuid);

	void create(AbstractDomain domain, MailConfig cfg) throws BusinessException;

	void update(MailConfig cfg) throws BusinessException;

	void delete(MailConfig cfg) throws BusinessException;
}
