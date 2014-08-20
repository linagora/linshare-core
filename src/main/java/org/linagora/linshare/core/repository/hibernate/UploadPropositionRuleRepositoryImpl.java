package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadPropositionRuleRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UploadPropositionRuleRepositoryImpl extends
		AbstractRepositoryImpl<UploadPropositionRule> implements
		UploadPropositionRuleRepository {

	public UploadPropositionRuleRepositoryImpl(
			HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(
			UploadPropositionRule entity) {
		DetachedCriteria det = DetachedCriteria.forClass(
				UploadPropositionRule.class).add(
				Restrictions.eq("id", entity.getId()));
		return det;
	}

	@Override
	public UploadPropositionRule find(String uuid) {
		List<UploadPropositionRule> entries = findByCriteria(Restrictions.eq(
				"uuid", uuid));
		return DataAccessUtils.requiredSingleResult(entries);
	}

	@Override
	public UploadPropositionRule create(UploadPropositionRule entity)
			throws BusinessException, IllegalArgumentException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadPropositionRule update(UploadPropositionRule entity)
			throws BusinessException, IllegalArgumentException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

}
