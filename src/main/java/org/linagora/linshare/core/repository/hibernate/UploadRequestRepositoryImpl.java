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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

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
	public List<UploadRequest> findByOwner(User owner, List<UploadRequestStatus> statusList) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("owner", owner));
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
		Junction add = Restrictions.conjunction()
						.add(Restrictions.in("abstractDomain", domains))
						.add(Restrictions.between("creationDate", after, before));
		if (!status.isEmpty()) {
			add.add(Restrictions.in("status", status));
		}
		return findByCriteria(add);
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
		crit.add(Restrictions.eq("status", UploadRequestStatus.STATUS_ENABLED));
		crit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public List<String> findCreatedUploadRequests() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.add(Restrictions.lt("activationDate", new Date()));
		crit.add(Restrictions.eq("status", UploadRequestStatus.STATUS_CREATED));
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
		crit.add(Restrictions.eq("status", UploadRequestStatus.STATUS_ENABLED));
		crit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}
}
