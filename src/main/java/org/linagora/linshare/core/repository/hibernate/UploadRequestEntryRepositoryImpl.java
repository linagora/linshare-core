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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.utils.DocumentCount;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

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

	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentCount> findByDomainsBetweenTwoDates(AbstractDomain domain, Calendar beginDate, Calendar endDate) {
		ProjectionList projections = Projections.projectionList()
				.add(Projections.groupProperty("type").as("mimeType"))
				.add(Projections.sum("size").as("totalSize"))
				.add(Projections.alias(Projections.rowCount(), "total"));
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass())
				.createAlias("uploadRequestUrl", "uploadRequestUrl")
				.createAlias("uploadRequestUrl.uploadRequest", "uploadRequest")
				.createAlias("uploadRequest.uploadRequestGroup", "uploadRequestGroup")
				.setProjection(projections);
		criteria.add(Restrictions.eq("uploadRequestGroup.abstractDomain", domain));
		criteria.add(Restrictions.lt("creationDate", endDate));
		criteria.add(Restrictions.gt("creationDate", beginDate));
		criteria.setResultTransformer(new AliasToBeanResultTransformer(DocumentCount.class));
		List<DocumentCount> mimeTypes = listByCriteria(criteria);
		return mimeTypes;
	}

	@Override
	public List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.add(Restrictions.eq("uploadRequestUrl", uploadRequestUrl));
		return findByCriteria(det);
	}

	@Override
	public long getRelatedUploadRequestEntryCount(Document document) {
		DetachedCriteria det = DetachedCriteria.forClass(UploadRequestEntry.class);
		det.add(Restrictions.eq("document", document));
		det.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(det));
	}

	@Override
	public List<UploadRequestEntry> findAllEntries(UploadRequest uploadRequest) {
		DetachedCriteria urCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequestEntry");
		urCrit.createAlias("uploadRequestEntry.uploadRequestUrl", "url");
		urCrit.add(Restrictions.eq("url.uploadRequest", uploadRequest));
		@SuppressWarnings("unchecked")
		List<UploadRequestEntry> list = listByCriteria(urCrit);
		return list;
	}

	@Override
	public Boolean exist(UploadRequest uploadRequest, String entryUuid) {
		DetachedCriteria urCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequestEntry");
		urCrit.createAlias("uploadRequestEntry.uploadRequestUrl", "url");
		urCrit.add(Restrictions.eq("url.uploadRequest", uploadRequest));
		urCrit.add(Restrictions.eq("uuid", entryUuid));
		urCrit.setProjection(Projections.rowCount());
		Long longResult = DataAccessUtils.longResult(findByCriteria(urCrit));
		return longResult > 0;
	}

	@Override
	public List<String> findAllEntriesForArchivedDeletedPurgedUR() {
		DetachedCriteria urCrit = DetachedCriteria.forClass(getPersistentClass(), "uploadRequestEntry");
		urCrit.createAlias("uploadRequestEntry.uploadRequestUrl", "url");
		urCrit.createAlias("url.uploadRequest", "uploadRequest");
		Disjunction or = Restrictions.disjunction();
		urCrit.add(or);
		or.add(Restrictions.eq("uploadRequest.status", UploadRequestStatus.ARCHIVED));
		or.add(Restrictions.eq("uploadRequest.status", UploadRequestStatus.DELETED));
		or.add(Restrictions.eq("uploadRequest.status", UploadRequestStatus.PURGED));
		urCrit.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(urCrit);
		return list;
	}
}
