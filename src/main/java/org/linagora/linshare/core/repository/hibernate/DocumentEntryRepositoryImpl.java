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


import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DocumentEntryRepositoryImpl extends AbstractRepositoryImpl<DocumentEntry> implements DocumentEntryRepository {

	public DocumentEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DocumentEntry entry) {
		DetachedCriteria det = DetachedCriteria.forClass(DocumentEntry.class).add(Restrictions.eq( "uuid", entry.getUuid()));
		return det;
	}

	 /** Find a document using its id.
     * @param id
     * @return found document (null if no document found).
     */
	@Override
    public DocumentEntry findById(String uuid) {
        List<DocumentEntry> entries = findByCriteria(Restrictions.eq("uuid", uuid));
        if (entries == null || entries.isEmpty()) {
            return null;
        } else if (entries.size() == 1) {
            return entries.get(0);
        } else {
            throw new IllegalStateException("Id must be unique");
        }
    }

	@Override
	public List<DocumentEntry> findAllMyDocumentEntries(Account owner) {
		return findByCriteria(Restrictions.eq("entryOwner", owner));
	}

	@Override
	public List<DocumentEntry> findAllMySyncEntries(Account owner) {
		DetachedCriteria crit = DetachedCriteria.forClass(DocumentEntry.class);
		crit.add(Restrictions.eq("entryOwner", owner));
		crit.add(Restrictions.eq("cmisSync", true));
		return findByCriteria(crit);
	}

	@Override
	public DocumentEntry create(DocumentEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	@Override
	public long getRelatedEntriesCount(final DocumentEntry documentEntry) {
		long result  = 0 ;

		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery("select count(*) from ShareEntry s where s.documentEntry = :documentEntry");
				query.setParameter("documentEntry", documentEntry);
				return 	((Long)query.iterate().next()).longValue();
			}
		};
		Long shareResult = getHibernateTemplate().execute(action);

		action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery("select count(*) from AnonymousShareEntry s where s.documentEntry = :documentEntry");
				query.setParameter("documentEntry", documentEntry);
				return 	((Long)query.iterate().next()).longValue();
			}
		};
		Long anonymousShareResult = getHibernateTemplate().execute(action);

		result = anonymousShareResult + shareResult;
		if(logger.isDebugEnabled())
			logger.debug("related entries for document " + documentEntry.getUuid() + " :  (share=" + shareResult + ", anonymous=" + anonymousShareResult + " , sum=" + result + ")");

		return result;
	}

	@Override
	public List<String> findAllExpiredEntries() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.add(Restrictions.lt("expirationDate", Calendar.getInstance()));
		@SuppressWarnings("unchecked")
		List<String> list = listByCriteria(criteria);
		return list;
	}

	@Override
	public long getUsedSpace(Account owner) throws BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(DocumentEntry.class);
		det.add(Restrictions.eq("entryOwner", owner));
		det.createAlias("document", "doc");
		ProjectionList columns = Projections.projectionList()
				.add(Projections.sum("doc.size"));
		det.setProjection(columns);
		List<DocumentEntry> result = findByCriteria(det);
		if (result == null || result.isEmpty() || result.get(0) == null) {
			return 0;
		}
		return DataAccessUtils.longResult(result);
	}

	@Override
	public DocumentEntry findMoreRecentByName(Account owner, String fileName) {
		DetachedCriteria det = DetachedCriteria.forClass(DocumentEntry.class);
		det.add(Restrictions.eq("entryOwner", owner));
		det.add(Restrictions.eq("name", fileName));
		det.add(Restrictions.eq("cmisSync", true));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@Override
	public void syncUniqueDocument(final Account owner, final String fileName)
			throws BusinessException {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery("UPDATE Entry SET cmisSync = false WHERE name = :fileName and entryOwner = :owner");
				query.setParameter("owner", owner);
				query.setParameter("fileName", fileName);
				return (long) query.executeUpdate();
			}
		};
		getHibernateTemplate().execute(action);
	}
}
