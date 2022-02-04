/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
}
