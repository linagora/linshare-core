package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadPropositionFilterRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UploadPropositionFilterRepositoryImpl extends AbstractRepositoryImpl<UploadPropositionFilter> implements
		UploadPropositionFilterRepository {

	public UploadPropositionFilterRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UploadPropositionFilter entity) {
		DetachedCriteria det = DetachedCriteria.forClass(UploadPropositionFilter.class).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public UploadPropositionFilter find(String uuid) {
		List<UploadPropositionFilter> entries = findByCriteria(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.requiredSingleResult(entries);
	}

	@Override
	public UploadPropositionFilter create(UploadPropositionFilter entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadPropositionFilter update(UploadPropositionFilter entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}
	

}