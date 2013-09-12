package org.linagora.linshare.core.repository.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailingListContactRepositoryImpl extends AbstractRepositoryImpl<MailingListContact> implements MailingListContactRepository{

	private static final Logger logger = LoggerFactory.getLogger(MailingListRepositoryImpl.class);

	public MailingListContactRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	public MailingListContact findById(long id) {
		List<MailingListContact> mailingList = findByCriteria(Restrictions.eq("id", id));
		if (mailingList == null || mailingList.isEmpty()) {
			return null;
		} else if (mailingList.size() == 1) {
			return mailingList.get(0);
		} else {
			throw new IllegalStateException("Id must be unique");
		}
	}

	@Override
	public MailingListContact findByMail(final MailingList list, final String mail) {
//		DetachedCriteria det = DetachedCriteria.forClass(MailingListContact.class);
//		det.add(Restrictions.and(Restrictions.eq("mailing_list_id", list.getPersistenceId()), Restrictions.eq("mail", mail)));
//		
//		List<MailingListContact> mailingList = findByCriteria(det);
//		if (mailingList == null || mailingList.isEmpty()) {
//			return null;
//		} else if (mailingList.size() == 1) {
//			return mailingList.get(0);
//		} else {
//			throw new IllegalStateException("Id must be unique");
//		}
		/*
		HibernateCallback<MailingListContact> action = new HibernateCallback<MailingListContact>() {
		public MailingListContact doInHibernate(final Session session) throws HibernateException, SQLException {
			final Query query = session.createQuery("select * from MailingListContact m where m.mailing_list_id = :mailing_list_id and m.mail = :mail");
			query.setParameter("mailing_list_id", list.getPersistenceId());
			query.setParameter("mail", mail);
				return 	((MailingListContact)query.iterate().next());
			}	
		};
		*/
		DetachedCriteria contacts = DetachedCriteria.forClass(MailingList.class);
		DetachedCriteria det = DetachedCriteria.forClass(MailingListContact.class);
		List<MailingListContact> res = new ArrayList<MailingListContact>();

		contacts.add(Restrictions.eq("id", list.getPersistenceId()));
		contacts.setProjection(Property.forName("mailingListContact"));
		det.add(Property.forName("id").in(contacts));
		det.add(Restrictions.eq("mail", mail));
		
		res = findByCriteria(det);
		
		if (res == null || res.isEmpty()) {
			return null;
		} else if (res.size() == 1) {
			return res.get(0);
		} else {
			throw new IllegalStateException("mail are unique in a mailing list");
		}
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailingListContact entity) {
		DetachedCriteria det = DetachedCriteria.forClass(MailingListContact.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}
	
	public MailingListContact update(MailingListContact entity) throws BusinessException {
	   // getHibernateTemplate().merge(entity);
		return super.update(entity);
	}
}
