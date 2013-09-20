package org.linagora.linshare.core.repository.hibernate;

import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.MailingListContact;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.MailingListContactRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class MailingListContactRepositoryImpl extends AbstractRepositoryImpl<MailingListContact> implements
		MailingListContactRepository {

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
	protected DetachedCriteria getNaturalKeyCriteria(MailingListContact entity) {
		DetachedCriteria det = DetachedCriteria.forClass(MailingListContact.class).add(
				Restrictions.eq("id", entity.getPersistenceId()));
		return det;
	}

	@Override
	public MailingListContact update(MailingListContact entity) throws BusinessException {
		// entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public MailingListContact create(MailingListContact entity) throws BusinessException {
		// entity.setCreationDate(new Date());
		// entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}
}
