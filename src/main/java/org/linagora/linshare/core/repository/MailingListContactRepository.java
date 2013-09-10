package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;

public interface MailingListContactRepository extends AbstractRepository<MailingListContact> {

	public MailingListContact findByMail(MailingList list, String mail);
	
	public MailingListContact findById(long id);
}
