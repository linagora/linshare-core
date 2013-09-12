package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;

public interface MailingListContactRepository extends AbstractRepository<MailingListContact> {

	public MailingListContact findByMail(final MailingList list, final String mail);
	
	public MailingListContact findById(long id);
}
