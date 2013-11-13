package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailingListContactRepositoryImpl extends
		AbstractRepositoryImpl<MailingListContact> implements
		MailingListContactRepository {

	public MailingListContactRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(MailingListContact entity) {
		return DetachedCriteria.forClass(MailingListContact.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
	}

	@Override
	public MailingListContact findById(long id) {
		DetachedCriteria det = DetachedCriteria
				.forClass(MailingListContact.class);

		det.add(Restrictions.eq("id", id));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public MailingListContact findByUuid(String uuid) {
		DetachedCriteria det = DetachedCriteria
				.forClass(MailingListContact.class);

		det.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public MailingListContact findByMail(MailingList list, String mail) {
		DetachedCriteria det = DetachedCriteria
				.forClass(MailingListContact.class);

		det.add(Restrictions.eq("mail", mail));
		det.add(Restrictions.eq("mailingList", list));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllContactMails(MailingList list) {
		DetachedCriteria det = DetachedCriteria.forClass(MailingListContact.class);

		det.add(Restrictions.eq("mailingList", list));
		det.setProjection(Projections.property("mail"));
		return (List<String>) listByCriteria(det);
	}

	@Override
	public MailingListContact update(MailingListContact entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public MailingListContact create(MailingListContact entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}
}
