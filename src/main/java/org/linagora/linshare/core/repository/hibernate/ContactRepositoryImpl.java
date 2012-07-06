package org.linagora.linshare.core.repository.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.repository.ContactRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ContactRepositoryImpl extends AbstractRepositoryImpl<Contact> implements ContactRepository {

	public ContactRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Contact findByMail(String mail) {
		List<Contact> users = findByCriteria(Restrictions.eq("mail", mail).ignoreCase());
        if (users == null || users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new IllegalStateException("Mail must be unique");
        }
	}
	
	

	@Override
	public Contact find(Contact contact) {
		String mail = contact.getMail();
		if(mail == null) {
			 throw new IllegalStateException("Mail must be set");
		}
		return findByMail(mail);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Contact entity) {
		DetachedCriteria det = DetachedCriteria.forClass(Contact.class).add(Restrictions.eq("mail", entity.getMail()));
		return det;
	}

}
