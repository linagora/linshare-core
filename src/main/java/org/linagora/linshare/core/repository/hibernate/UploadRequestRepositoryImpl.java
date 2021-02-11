/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class UploadRequestRepositoryImpl extends
		AbstractRepositoryImpl<UploadRequest> implements
		UploadRequestRepository {

	public UploadRequestRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UploadRequest req) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("uuid", req.getUuid()));
		return det;
	}

	@Override
	public UploadRequest findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public List<UploadRequest> findAll(UploadRequestGroup uploadRequestGroup, List<UploadRequestStatus> statusList) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("uploadRequestGroup", uploadRequestGroup));
		if (statusList != null && !statusList.isEmpty()) {
			det.add(Restrictions.in("status", statusList));
		}
		return findByCriteria(det);
	}

	@Override
	public List<UploadRequest> findByStatus(UploadRequestStatus... status) {
		return findByCriteria(Restrictions.in("status", status));
	}

	@Override
	public List<UploadRequest> findByDomainsAndStatus(
			List<AbstractDomain> domains, List<UploadRequestStatus> status,
			Date after, Date before) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.createAlias("uploadRequestGroup", "urg");
		det.add(Restrictions.conjunction()
						.add(Restrictions.in("urg.abstractDomain", domains))
						.add(Restrictions.between("creationDate", after, before)));
		if (!status.isEmpty()) {
			det.add(Restrictions.in("status", status));
		}
		return findByCriteria(det);
	}

	@Override
	public UploadRequest create(UploadRequest entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadRequest update(UploadRequest entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public List<String> findOutdatedRequests() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.add(Restrictions.lt("expiryDate", new Date()));
		crit.add(Restrictions.eq("status", UploadRequestStatus.ENABLED));
		crit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public List<String> findCreatedUploadRequests() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.add(Restrictions.lt("activationDate", new Date()));
		crit.add(Restrictions.eq("status", UploadRequestStatus.CREATED));
		crit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public List<String> findAllRequestsToBeNotified() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(GregorianCalendar.HOUR_OF_DAY, 0);
		gc.set(GregorianCalendar.MINUTE, 0);
		gc.set(GregorianCalendar.SECOND, 0);
		gc.set(GregorianCalendar.MILLISECOND, 0);
		Date before = gc.getTime();
		gc.add(GregorianCalendar.DAY_OF_MONTH, 1);
		Date after = gc.getTime();
		crit.add(Restrictions.lt("notificationDate", after));
		crit.add(Restrictions.gt("notificationDate", before));
		crit.add(Restrictions.ltProperty("notificationDate", "expiryDate"));
		crit.add(Restrictions.eq("status", UploadRequestStatus.ENABLED));
		crit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public Integer countNbrUploadedFiles(UploadRequest uploadRequest) {
		DetachedCriteria urCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequest");
		urCrit.createAlias("uploadRequest.uploadRequestURLs", "urls");
		urCrit.add(Restrictions.eq("urls.uploadRequest", uploadRequest));
		urCrit.createAlias("urls.uploadRequestEntries", "entries");
		urCrit.setProjection(Projections.rowCount());
		Number nbrUploadedFiles = (Number) urCrit.getExecutableCriteria(getCurrentSession()).uniqueResult();
		return nbrUploadedFiles.intValue();
	}

	@Override
	public Long computeEntriesSize(UploadRequest uploadRequest) {
		DetachedCriteria urCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequest");
		urCrit.createAlias("uploadRequest.uploadRequestURLs", "urls");
		urCrit.add(Restrictions.eq("urls.uploadRequest", uploadRequest));
		urCrit.createAlias("urls.uploadRequestEntries", "entries");
		urCrit.setProjection(Projections.sum("entries.size"));
		List<UploadRequest> list = findByCriteria(urCrit);
		if (list.size() > 0 && list.get(0) != null) {
			return DataAccessUtils.longResult(list);
		}
		return 0L;
	}

	@Override
	public List<UploadRequest> findUploadRequestsToUpdate(UploadRequestGroup uploadRequestGroup,
			List<UploadRequestStatus> listAllowedStatusToUpdate) {
		DetachedCriteria urCrit = DetachedCriteria.forClass(getPersistentClass());
		urCrit.add(Restrictions.eq("uploadRequestGroup", uploadRequestGroup));
		urCrit.add(Restrictions.in("status", listAllowedStatusToUpdate));
		return findByCriteria(urCrit);
	}
}
