package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Contact;

public interface ContactRepository extends AbstractRepository<Contact> {

	public Contact findByMail(String mail);
	
	public Contact find(Contact contact);
	
}
