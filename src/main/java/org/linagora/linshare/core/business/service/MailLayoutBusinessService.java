package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailLayout;

public interface MailLayoutBusinessService {

	MailLayout findByUuid(String uuid);

	void create(AbstractDomain domain, MailLayout layout);

	void update(MailLayout layout);

	void delete(MailLayout val);

}
