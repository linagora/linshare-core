/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.core.repository.hibernate;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.utils.QueryParameter;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DocumentEntryRepositoryImpl extends AbstractRepositoryImpl<DocumentEntry> implements DocumentEntryRepository {
	
	public DocumentEntryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(DocumentEntry aDoc) {
		DetachedCriteria det = DetachedCriteria.forClass(DocumentEntry.class).add(Restrictions.eq( "uuid", aDoc.getUuid()) );
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
		List<DocumentEntry> entries = findByCriteria(Restrictions.eq("entryOwner", owner));
        if (entries == null) {
            return null;
        }
        return entries;
	}

	
	@Override
	public DocumentEntry create(DocumentEntry entity) throws BusinessException {
		entity.setCreationDate(new GregorianCalendar());
		entity.setModificationDate(new GregorianCalendar());
		entity.setUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	
	@Override
	public DocumentEntry update(DocumentEntry entity) throws BusinessException {
		entity.setModificationDate(new GregorianCalendar());
		return super.update(entity);
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
		Long shareResult = (Long) getHibernateTemplate().execute(action);
		
		action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session) throws HibernateException, SQLException {
				final Query query = session.createQuery("select count(*) from AnonymousShareEntry s where s.documentEntry = :documentEntry");
				query.setParameter("documentEntry", documentEntry);
				return 	((Long)query.iterate().next()).longValue();
			}
		};
		Long anonymousShareResult = (Long) getHibernateTemplate().execute(action);
		
		result = anonymousShareResult + shareResult;
		if(logger.isDebugEnabled())
			logger.debug("related entries for document " + documentEntry.getUuid() + " :  (share=" + shareResult + ", anonymous=" + anonymousShareResult + " , sum=" + result + ")");
		
		return result;
	}
	
	

	@Override
	public List<DocumentEntry> findAllExpiredEntries() {
		List<DocumentEntry> entries = findByCriteria(Restrictions.lt("expirationDate", Calendar.getInstance()));
        if (entries == null) {
        	logger.error("the result is null ! this should not happen.");
            return new ArrayList<DocumentEntry>();
        }
        return entries;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DocumentEntry> retrieveUserDocumentEntriesWithMatchCriterion(SearchDocumentCriterion searchDocumentCriterion) {
		
		
		final QueryParameter queryParameter = buildQuery(searchDocumentCriterion, ANYWHERE);
		
		return (List<DocumentEntry>)getHibernateTemplate().executeFind(new HibernateCallback<List<DocumentEntry>>() {
			public List<DocumentEntry> doInHibernate(final Session session) throws HibernateException, SQLException {
				
				StringBuilder queryString = new StringBuilder("select docEntry from DocumentEntry docEntry join docEntry.entryOwner account join docEntry.document doc ");
//				StringBuilder queryString = new StringBuilder("select doc from DocumentEntry docEntry ");
				
				final Query query = session.createQuery(queryString.append(queryParameter.getQuery()).toString());
				
				// Put the objects in the query
				for (String key : queryParameter.getKey()) {
					query.setParameter(key, queryParameter.getParameter(key));
				}
				
				return query.setCacheable(true).list();
			}
		});
	}
	
	
	
	/**
	 * Build the search query
	 * @param searchDocumentCriterion
	 * @param matcher
	 * @return
	 */
	private QueryParameter buildQuery(final SearchDocumentCriterion searchDocumentCriterion, final int matcher) {
		
		QueryParameter queryParameter = new QueryParameter();
		queryParameter.appendToQuery(" docEntry.entryOwner.lsUuid=:lsUuid " );
		queryParameter.addParameter("lsUuid", searchDocumentCriterion.getUser().getLsUid());
		
		if(null!=searchDocumentCriterion.getName()){
			queryParameter.appendToQuery(" lower(docEntry.name) like lower(:name) " );
			queryParameter.addParameter("name", createMatchingCriteria(matcher,searchDocumentCriterion.getName()));
		}
		
		if(null!=searchDocumentCriterion.getExtension()){
			queryParameter.appendToQuery(" lower(docEntry.name) like lower(:extension) " );
			queryParameter.addParameter("extension", createMatchingCriteria(END,searchDocumentCriterion.getExtension()));
		}
		
		// TODO
//		if(null!=searchDocumentCriterion.isShared()){
//			queryParameter.appendToQuery(" doc.shared=:shared " );
//			queryParameter.addParameter("shared", searchDocumentCriterion.isShared());
//		}
		
		if(null!=searchDocumentCriterion.getType() && !"".equals(searchDocumentCriterion.getType())){
			queryParameter.appendToQuery(" doc.type like :type " );
			queryParameter.addParameter("type", createMatchingCriteria(matcher,searchDocumentCriterion.getType()));
		}
		
		if(null!=searchDocumentCriterion.getSizeMin()){
			queryParameter.appendToQuery(" doc.size>=:sizeMin " );
			queryParameter.addParameter("sizeMin", searchDocumentCriterion.getSizeMin());
		}
		
		if(null!=searchDocumentCriterion.getSizeMax()){
			queryParameter.appendToQuery(" doc.size<=:sizeMax " );
			queryParameter.addParameter("sizeMax", searchDocumentCriterion.getSizeMax());
		}

		
		if(null!=searchDocumentCriterion.getDateBegin()){
			queryParameter.appendToQuery(" docEntry.creationDate>=:creationDateBegin " );
			queryParameter.addParameter("creationDateBegin", searchDocumentCriterion.getDateBegin());
	
		}
		
		if(null!=searchDocumentCriterion.getDateEnd()){
			queryParameter.appendToQuery(" docEntry.creationDate<=:creationDateEnd " );
			queryParameter.addParameter("creationDateEnd", searchDocumentCriterion.getDateEnd());

		}

		return queryParameter;
	}
	

	private String createMatchingCriteria(int matcher,String value ) {
		switch(matcher){
		case BEGIN: return value+"%";
		case END:return "%"+value ;
		case ANYWHERE: return "%"+value+"%";
		}
		return value;
	}

	
}
