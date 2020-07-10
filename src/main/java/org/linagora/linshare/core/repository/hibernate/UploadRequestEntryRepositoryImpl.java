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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.collect.Maps;

public class UploadRequestEntryRepositoryImpl extends
		AbstractRepositoryImpl<UploadRequestEntry> implements
		UploadRequestEntryRepository {

	public UploadRequestEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(UploadRequestEntry entry) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass()).add(
				Restrictions.eq("uuid", entry.getUuid()));
		return det;
	}

	@Override
	public UploadRequestEntry findByUuid(String uuid) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"uuid", uuid)));
	}

	@Override
	public UploadRequestEntry create(UploadRequestEntry entity)
			throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public UploadRequestEntry update(UploadRequestEntry entity)
			throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
	}

	@Override
	public UploadRequestEntry findRelative(DocumentEntry entry) {
		return DataAccessUtils.singleResult(findByCriteria(Restrictions.eq(
				"documentEntry", entry)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Long> findByDomainsBetweenTwoDates(AbstractDomain domain, Calendar beginDate, Calendar endDate) {
		Map<String, Long> results = Maps.newHashMap();
		ProjectionList projections = Projections.projectionList()
				.add(Projections.groupProperty("type"))
				.add(Projections.rowCount());
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass())
				.createAlias("uploadRequestUrl", "uploadRequestUrl")
				.createAlias("uploadRequestUrl.uploadRequest", "uploadRequest")
				.createAlias("uploadRequest.uploadRequestGroup", "uploadRequestGroup")
				.setProjection(projections);
		criteria.add(Restrictions.eq("uploadRequestGroup.abstractDomain", domain));
		criteria.add(Restrictions.lt("creationDate", endDate));
		criteria.add(Restrictions.gt("creationDate", beginDate));
		List<Object[]> list = listByCriteria(criteria);
		list.stream().forEach(e -> results.put((String) e[0], (Long) e[1]));
		return results;
	}

	@Override
	public List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("uploadRequestUrl", uploadRequestUrl));
		return findByCriteria(det);
	}

	@Override
	public Long computeEntriesSize(UploadRequestUrl uploadRequestUrl) {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException {
				@SuppressWarnings("unchecked")
				final Query<Long> query = session.createQuery(
						"select SUM (size) from UploadRequestEntry u where u.uploadRequestUrl = :uploadRequestUrl");
				query.setParameter("uploadRequestUrl", uploadRequestUrl);
				if (query.list() == null || query.list().get(0) == null) {
					return 0L;
				} else {
					return query.list().get(0);
				}
			}
		};
		Long result = getHibernateTemplate().execute(action);
		return result;
	}
}
