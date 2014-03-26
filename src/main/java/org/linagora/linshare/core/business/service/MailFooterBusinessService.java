package org.linagora.linshare.core.business.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailFooter;

public interface MailFooterBusinessService {

	MailFooter findByUuid(String uuid);

	void create(AbstractDomain domain, MailFooter footer);

	void update(MailFooter footer);

	void delete(MailFooter val);

}
