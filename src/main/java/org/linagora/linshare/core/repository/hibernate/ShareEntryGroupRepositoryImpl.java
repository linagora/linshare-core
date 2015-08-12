package org.linagora.linshare.core.repository.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class ShareEntryGroupRepositoryImpl extends AbstractRepositoryImpl<ShareEntryGroup>
		implements ShareEntryGroupRepository {

	public ShareEntryGroupRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(ShareEntryGroup entity) {
		DetachedCriteria det = DetachedCriteria.forClass(ShareEntryGroup.class)
				.add(Restrictions.eq("uuid", entity.getUuid()));
		return det;
	}

	@Override
	public ShareEntryGroup create(ShareEntryGroup entity)
			throws BusinessException, IllegalArgumentException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public ShareEntryGroup update(ShareEntryGroup entity)
			throws BusinessException, IllegalArgumentException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public ShareEntryGroup findById(long id) {
		return DataAccessUtils
				.singleResult(findByCriteria(Restrictions.eq("id", id)));
	}

	@Override
	public ShareEntryGroup findByUuid(String uuid) {
		return DataAccessUtils
				.singleResult(findByCriteria(Restrictions.eq("uuid", uuid)));
	}

	@Override
	public List<ShareEntryGroup> findAllToNotify(){
		Date dt = Calendar.getInstance().getTime();
		List<ShareEntryGroup> shareEntriesGroup = findByCriteria(
				Restrictions.lt("notificationDate", dt));
		if (shareEntriesGroup == null) {
			logger.info("No shareEntryGroup has an expired notification date");
			return new ArrayList<ShareEntryGroup>();
		}
		return shareEntriesGroup;
	}

	@Override
	public List<ShareEntryGroup> findAllToPurge() {
		List<ShareEntryGroup> shareEntriesGroup = findByCriteria(
				Restrictions.eq("notified", true));
		if (shareEntriesGroup == null) {
			logger.info("No shareEntryGroup to purge");
			return new ArrayList<ShareEntryGroup>();
		}
		return shareEntriesGroup;
	}
}
