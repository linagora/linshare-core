/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.repository.hibernate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestGroupRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UploadRequestGroupRepositoryImpl extends
		AbstractRepositoryImpl<UploadRequestGroup> implements
		UploadRequestGroupRepository {

	public UploadRequestGroupRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public List<UploadRequestGroup> findAllByOwner(Account owner, List<UploadRequestStatus> uploadRequestStatus) {
		DetachedCriteria cri = DetachedCriteria.forClass(getPersistentClass());
		cri.add(Restrictions.eq("owner", owner));
		cri.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		if (uploadRequestStatus != null && !uploadRequestStatus.isEmpty()) {
			cri.add(Restrictions.in("status", uploadRequestStatus));
		}
		return findByCriteria(cri);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UploadRequestGroup group) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("uuid", group.getUuid()));
		return det;
	}

	@Override
	public UploadRequestGroup findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public UploadRequestGroup create(UploadRequestGroup entity)
			throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadRequestGroup update(UploadRequestGroup entity)
			throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public List<String> findOutDateRequests() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.add(Restrictions.lt("expiryDate", new Date()));
		crit.add(Restrictions.eq("status", UploadRequestStatus.ENABLED));
		crit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public Integer countNbrUploadedFiles(UploadRequestGroup uploadRequestGroup) {
		DetachedCriteria urgCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequestGroup");
		urgCrit.createAlias("uploadRequestGroup.uploadRequests", "uploadRequests");
		urgCrit.add(Restrictions.eq("uploadRequests.uploadRequestGroup", uploadRequestGroup));
		urgCrit.createAlias("uploadRequests.uploadRequestURLs", "urls");
		urgCrit.createAlias("urls.uploadRequestEntries", "entries");
		urgCrit.setProjection(Projections.rowCount());
		Number nbrUploadedFiles = (Number) urgCrit.getExecutableCriteria(getCurrentSession()).uniqueResult();
		return nbrUploadedFiles.intValue();
	}

	@Override
	public Long computeEntriesSize(UploadRequestGroup uploadRequestGroup) {
		DetachedCriteria urgCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequestGroup");
		urgCrit.createAlias("uploadRequestGroup.uploadRequests", "uploadRequests");
		urgCrit.add(Restrictions.eq("uploadRequests.uploadRequestGroup", uploadRequestGroup));
		urgCrit.createAlias("uploadRequests.uploadRequestURLs", "urls");
		urgCrit.createAlias("urls.uploadRequestEntries", "entries");
		urgCrit.setProjection(Projections.sum("entries.size"));
		List<UploadRequestGroup> list = findByCriteria(urgCrit);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(list);
		}
		return 0L;
	}

	@Override
	public Long computeURGcount(Account owner) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.setProjection(Projections.rowCount());
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.or(
			Restrictions.eq("status", UploadRequestStatus.CREATED),
			Restrictions.eq("status", UploadRequestStatus.ENABLED),
			Restrictions.eq("status", UploadRequestStatus.CLOSED)
		));
		return DataAccessUtils.longResult(findByCriteria(det));
	}

	@Override
	public List<UploadRequestGroup> findAllByAccountAndDomain(final Account owner, final AbstractDomain abstractDomain) {
		final DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.eq("abstractDomain", abstractDomain));
		return this.findByCriteria(det);
	}
}
