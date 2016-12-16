/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.BatchHistory;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.BatchHistoryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class BatchHistoryRepositoryImpl extends AbstractRepositoryImpl<BatchHistory>implements BatchHistoryRepository {

	public BatchHistoryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public List<BatchHistory> find(Date beginDate, Date endDate, BatchType batchType, String status) {
		// TODO FIXME Quota & Statistics
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null) {
			criteria.add(Restrictions.ge("executionDate", beginDate));
		} if (endDate != null) {
			criteria.add(Restrictions.le("executionDate", endDate));
		} if (batchType != null) {
			criteria.add(Restrictions.eq("batchType", batchType));
		} if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		return findByCriteria(criteria);
	}

	@Override
	public BatchHistory findByBatchType(Date beginDate, Date endDate, BatchType batchType) throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null) {
			criteria.add(Restrictions.ge("executionDate", beginDate));
		} if (endDate != null) {
			criteria.add(Restrictions.le("executionDate", endDate));
		}
		criteria.add(Restrictions.eq("batchType", batchType));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}

	@Override
	public boolean exist(Date beginDate, BatchType batchType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.ge("executionDate", beginDate));
		criteria.add(Restrictions.le("executionDate", new Date()));
		criteria.add(Restrictions.eq("batchType", batchType));
		List<BatchHistory> list = findByCriteria(criteria);
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public BatchHistory create(BatchHistory entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		entity.setActiveDate(new Date());
		entity.setExecutionDate(new Date());
		entity.setStatus("starting");
		entity.setErrors(0L);
		entity.setUnhandledErrors(0L);
		return super.create(entity);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(BatchHistory entity) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		return criteria.add(Restrictions.eq("id", entity.getId()));
	}

	@Override
	public BatchHistory findByUuid(String uuid) throws BusinessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("uuid", uuid));
		return DataAccessUtils.singleResult(findByCriteria(criteria));
	}
}
