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


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

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
     * @param id
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
}
