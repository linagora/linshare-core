package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.UploadPropositionAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadPropositionActionRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UploadPropositionActionRepositoryImpl extends
		AbstractRepositoryImpl<UploadPropositionAction> implements
		UploadPropositionActionRepository {

	public UploadPropositionActionRepositoryImpl(
			HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(
			UploadPropositionAction entity) {
		DetachedCriteria det = DetachedCriteria.forClass(
				UploadPropositionAction.class).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public UploadPropositionAction find(String uuid) {
		List<UploadPropositionAction> entries = findByCriteria(Restrictions.eq(
				"uuid", uuid));
		return DataAccessUtils.requiredSingleResult(entries);
	}

	@Override
	public UploadPropositionAction create(UploadPropositionAction entity)
			throws BusinessException, IllegalArgumentException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadPropositionAction update(UploadPropositionAction entity)
			throws BusinessException, IllegalArgumentException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

}
