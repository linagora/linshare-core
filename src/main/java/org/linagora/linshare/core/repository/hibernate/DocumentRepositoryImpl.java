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


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class DocumentRepositoryImpl extends AbstractRepositoryImpl<Document> implements DocumentRepository {
	
	public DocumentRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(Document doc) {
		DetachedCriteria det = DetachedCriteria.forClass(Document.class)
			.add(Restrictions.eq("id", doc.getId()));
		return det;
	}
	
	
	 /** Find a document using its id.
     * @param identifier
     * @return found document (null if no document found).
     */
    public Document findByUuid(String identifier) {
        List<Document> documents = findByCriteria(Restrictions.eq("uuid", identifier));
        if (documents == null || documents.isEmpty()) {
            return null;
        } else if (documents.size() == 1) {
            return documents.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	@Override
	public List<String> findAllDocumentWithMimeTypeCheckEnabled() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.add(Restrictions.eq("checkMimeType", Boolean.TRUE));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<String> findAllIdentifiers() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public List<String> findAllSha256CheckNeededDocuments() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.setProjection(Projections.property("uuid"));
		crit.add(Restrictions.eq("sha256sum", "UNDEFINED"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public List<Document> findBySha256Sum(String sha256sum) {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.add(Restrictions.eq("sha256sum", sha256sum));
		crit.addOrder(Order.desc("creationDate"));
		@SuppressWarnings("unchecked")
		List<Document> list = listByCriteria(crit);
		return list;
	}

	@Override
	public List<String> findAllDocumentsToUpgrade() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.setProjection(Projections.property("uuid"));
		crit.add(Restrictions.eq("toUpgrade", true));
		crit.addOrder(Order.desc("creationDate"));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(crit);
		return list;
	}

	@Override
	public List<String> findAllDocumentWithComputeThumbnailEnabled() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.add(Restrictions.eq("computeThumbnail", Boolean.TRUE));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}
}
