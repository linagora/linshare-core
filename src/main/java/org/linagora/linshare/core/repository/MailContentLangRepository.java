package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailContentType;

public interface MailContentLangRepository extends
		AbstractRepository<MailContentLang> {

	MailContent findMailContent(MailConfig cfg, Language lang,
			MailContentType type);

	boolean isMailContentReferenced(MailContent content);
}
