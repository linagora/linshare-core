package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestEntryUrlRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class UploadRequestEntryUrlRepositoryImpl extends
		AbstractRepositoryImpl<UploadRequestEntryUrl> implements
		UploadRequestEntryUrlRepository {

	public UploadRequestEntryUrlRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UploadRequestEntryUrl uploadRequestEntryUrl) {
		DetachedCriteria det = DetachedCriteria.forClass(UploadRequestEntryUrl.class).add(
				Restrictions.eq("id", uploadRequestEntryUrl.getId()));
		return det;
	}

	@Override
	public UploadRequestEntryUrl findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public UploadRequestEntryUrl create(UploadRequestEntryUrl entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadRequestEntryUrl update(UploadRequestEntryUrl entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}
}
