package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;

public interface MailFooterLangRepository extends
		AbstractRepository<MailFooterLang> {

	boolean isMailFooterReferenced(MailFooter footer);
}
