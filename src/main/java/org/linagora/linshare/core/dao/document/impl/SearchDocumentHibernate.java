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
package org.linagora.linshare.core.dao.document.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.linagora.linshare.core.dao.document.SearchDocumentDao;
import org.linagora.linshare.core.domain.constants.DocumentType;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.vo.SearchDocumentCriterion;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class SearchDocumentHibernate implements SearchDocumentDao{

	private HibernateTemplate hibernateTemplate;

	
	private final static int BEGIN=0;
	private final static int END=1;
	private final static int ANYWHERE=2;
	
	public SearchDocumentHibernate(HibernateTemplate hibernateTemplate){
		this.hibernateTemplate=hibernateTemplate;
	}
	
	/*public List<Document> retrieveDocument(String login,UserRepository<User> userRepository) {
		
		return new ArrayList<Document>(userRepository.findByLogin(login).getDocuments());
	}*/
/*
	@SuppressWarnings("unchecked")
	public List<Document> retrieveDocumentWithCriterion(
			final SearchDocumentCriterion searchDocumentCriterion) {
		return (List<Document>)hibernateTemplate.execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria criteria=session.createCriteria(Document.class);
				
				if(null!=searchDocumentCriterion.getName()){
					criteria.add(Restrictions.eq("name", searchDocumentCriterion.getName()));
				}
				if(null!=searchDocumentCriterion.isShared()){
					criteria.add(Restrictions.eq("shared", searchDocumentCriterion.isShared()));
				}
				if(null!=searchDocumentCriterion.getType()){
					criteria.add(Restrictions.eq("type", searchDocumentCriterion.getType()));
				}
				if(null!=searchDocumentCriterion.getUser() && null!=searchDocumentCriterion.getUser().getLogin()){
					criteria.add(Restrictions.eq("ownerLogin", searchDocumentCriterion.getUser().getLogin()));
				}
				if(null!=searchDocumentCriterion.getSizeMin() || null!=searchDocumentCriterion.getSizeMax()){
					criteria.add(Restrictions.between("size", searchDocumentCriterion.getSizeMin(), searchDocumentCriterion.getSizeMax()));
				}
				if(null!=searchDocumentCriterion.getDateBegin() || null!=searchDocumentCriterion.getDateEnd()){
					
					if(null==searchDocumentCriterion.getDateBegin()){
						criteria.add(Restrictions.le("creationDate", searchDocumentCriterion.getDateEnd()));
					}else if(null==searchDocumentCriterion.getDateEnd()){
						criteria.add(Restrictions.ge("creationDate", searchDocumentCriterion.getDateBegin()));
					}else{
						criteria.add(Restrictions.between("creationDate", searchDocumentCriterion.getDateBegin(), searchDocumentCriterion.getDateEnd()));
					}
				}
				//criteria.add(Restrictions.eq("name", ))
				
				return criteria.list();
			}
		});
	}
*/
	
	/**
	 * not use this because it gives only a list of document, not a list of document + sharedocument
	 * use instead retrieveUserDocumentWithMatchCriterion() and retrieveUserReceivedSharedDocWithMatchCriterion()
	 */
	@Deprecated
	public List<Document> retrieveDocumentWithMatchCriterion(
			final SearchDocumentCriterion searchDocumentCriterion, final int matcher) {
		
		if (DocumentType.SHARED.equals(searchDocumentCriterion.getDocumentType())) {
			return retrieveUserReceivedDocWithMatchCriterion(searchDocumentCriterion,matcher);
		}
		
		if (DocumentType.OWNED.equals(searchDocumentCriterion.getDocumentType())) {
			return retrieveUserDocumentWithMatchCriterion(searchDocumentCriterion,matcher);
		}
		
		List<Document> listDoc = retrieveUserReceivedDocWithMatchCriterion(searchDocumentCriterion,matcher);
		listDoc.addAll(retrieveUserDocumentWithMatchCriterion(searchDocumentCriterion,matcher));
		
		return listDoc;
	}
	
	
	
	/**
	 * Retrieve the list of all the OWNED document
	 * @param searchDocumentCriterion
	 * @param matcher
	 * @return
	 */
	public List<Document> retrieveUserDocumentWithMatchCriterion(final SearchDocumentCriterion searchDocumentCriterion, final int matcher) {
		
		final QueryParameter queryParameter = buildQuery(searchDocumentCriterion, matcher);
		
		return (List<Document>)hibernateTemplate.executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
				
				StringBuilder queryString = new StringBuilder("select doc from Document doc join doc.owner u  ");
				
				
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
	 * Retrieve the list of all the RECEIVED document
	 * see retrieveUserReceivedSharedDocWithMatchCriterion()
	 * @deprecated
	 * @param searchDocumentCriterion
	 * @param matcher
	 * @return
	 */
	private List<Document> retrieveUserReceivedDocWithMatchCriterion(
			final SearchDocumentCriterion searchDocumentCriterion, final int matcher) {
		
		final QueryParameter queryParameter = buildQuery(searchDocumentCriterion, matcher);
		
		return (List<Document>)hibernateTemplate.executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
				
				StringBuilder queryString = new StringBuilder("select doc from User u join u.receivedShares shares join shares.document doc ");
				
				
				final Query query = session.createQuery(queryString.append(queryParameter.getQuery()).toString());
				
				// Put the objects in the query
				for (String key : queryParameter.getKey()) {
					query.setParameter(key, queryParameter.getParameter(key));
				}
				
				return query.setCacheable(true).list();
			}
		});
	}
	
	
//	/**
//	 * Retrieve the list of all the RECEIVED document
//	 * @param searchDocumentCriterion
//	 * @param matcher
//	 * @return share document
//	 */
//	public List<Share> retrieveUserReceivedSharedDocWithMatchCriterion(
//			final SearchDocumentCriterion searchDocumentCriterion, final int matcher) {
//		
//		final QueryParameter queryParameter = buildQuery(searchDocumentCriterion, matcher);
//		
//		return (List<Share>)hibernateTemplate.executeFind(new HibernateCallback() {
//			public Object doInHibernate(final Session session)
//			throws HibernateException, SQLException {
//				
//				
//				StringBuilder queryString = new StringBuilder("select shares from User u join u.receivedShares shares join shares.document doc ");
//				
//				
//				final Query query = session.createQuery(queryString.append(queryParameter.getQuery()).toString());
//				
//				// Put the objects in the query
//				for (String key : queryParameter.getKey()) {
//					query.setParameter(key, queryParameter.getParameter(key));
//				}
//				
//				return query.setCacheable(true).list();
//			}
//		});
//	}
	
	
	
	
	/**
	 * Build the seaarch query
	 * @param searchDocumentCriterion
	 * @param matcher
	 * @return
	 */
	private QueryParameter buildQuery(final SearchDocumentCriterion searchDocumentCriterion, final int matcher) {
		
		QueryParameter queryParameter = new QueryParameter();
		boolean where = false;
		
		queryParameter.addQuery(" where u.login=:login " );
		queryParameter.addParameter("login", searchDocumentCriterion.getUser().getLogin());
		where = true;
		
		if(null!=searchDocumentCriterion.getName()){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			queryParameter.addQuery(" lower(doc.name) like lower(:name) " );
			queryParameter.addParameter("name", createMatchingCriteria(matcher,searchDocumentCriterion.getName()));
		}
		
		if(null!=searchDocumentCriterion.getExtension()){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			queryParameter.addQuery(" lower(doc.name) like lower(:extension) " );
			queryParameter.addParameter("extension", createMatchingCriteria(END,searchDocumentCriterion.getExtension()));
		}
		
		if(null!=searchDocumentCriterion.isShared()){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			
			queryParameter.addQuery(" doc.shared=:shared " );
			queryParameter.addParameter("shared", searchDocumentCriterion.isShared());
		}
		
		if(null!=searchDocumentCriterion.getSharedFrom()){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			queryParameter.addQuery(" lower(doc.owner.mail) like lower(:sharedFrom) " );
			queryParameter.addParameter("sharedFrom", createMatchingCriteria(ANYWHERE,searchDocumentCriterion.getSharedFrom()));
		}
		
		
		
		if(null!=searchDocumentCriterion.getType() && !"".equals(searchDocumentCriterion.getType())){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			queryParameter.addQuery(" doc.type like :type " );
			queryParameter.addParameter("type", createMatchingCriteria(matcher,searchDocumentCriterion.getType()));
		}
		
		/* may only search within ones files
		if(null!=searchDocumentCriterion.getUser() && null!=searchDocumentCriterion.getUser().getLogin()){
			addMatcherToCriteria(criteria,matcher,"ownerLogin",searchDocumentCriterion.getUser().getLogin());
		}*/
		if(null!=searchDocumentCriterion.getSizeMin()){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			
			queryParameter.addQuery(" doc.size>=:sizeMin " );
			queryParameter.addParameter("sizeMin", searchDocumentCriterion.getSizeMin());
		}
		
		if(null!=searchDocumentCriterion.getSizeMax()){
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			
			queryParameter.addQuery(" doc.size<=:sizeMax " );
			queryParameter.addParameter("sizeMax", searchDocumentCriterion.getSizeMax());
		}

		
		if(null!=searchDocumentCriterion.getDateBegin()){
			
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			
			queryParameter.addQuery(" doc.creationDate>=:creationDateBegin " );
			queryParameter.addParameter("creationDateBegin", searchDocumentCriterion.getDateBegin());
	
		}
		
		if(null!=searchDocumentCriterion.getDateEnd()){
			
			if (!where) {
				queryParameter.addQuery(" where ");
				where = true;
			} else {
				queryParameter.addQuery(" and ");
			}
			
			queryParameter.addQuery(" doc.creationDate<=:creationDateEnd " );
			
			
			queryParameter.addParameter("creationDateEnd", searchDocumentCriterion.getDateEnd());

		}

		return queryParameter;
	}
	
	public int getBeginWith() {
		
		return BEGIN;
	}

	public int getEndWith() {
		
		return END;
	}

	public int getAnyWhere() {
		
		return ANYWHERE;
	}
	
	/*
	private void addMatcherToCriteria(Criteria criteria,int matcher,String property,String value){
		switch(matcher){
		case BEGIN: criteria.add(Restrictions.ilike(property, value,MatchMode.START));break;
		case END: criteria.add(Restrictions.ilike(property, value,MatchMode.END));break;
		case ANYWHERE: criteria.add(Restrictions.ilike(property, value,MatchMode.ANYWHERE));break;
		}
	}
	*/
	private String createMatchingCriteria(int matcher,String value ) {
		switch(matcher){
		case BEGIN: return value+"%";
		case END:return "%"+value ;
		case ANYWHERE: return "%"+value+"%";
		}
		return value;
	}

	

	/**
	 * Util class to store and build the where part of the query and the parameter
	 * associated
	 * 
	 * @author ncharles
	 * 
	 */
	class QueryParameter {
		StringBuilder query; // the query
		Map<String, Object> mapParam; // the paramters
		

		QueryParameter() {
			query = new StringBuilder();
			mapParam = new HashMap<String, Object>();
		}

		void addQuery(String aQuery) {
			query.append(aQuery);
		}

		public StringBuilder getQuery() {
			return query;
		}

		public Object getParameter(String aKey) {
			return mapParam.get(aKey);
		}

		public Set<String> getKey() {
			return mapParam.keySet();
		}

		public void addParameter(String aKey, Object theParameter) {
			mapParam.put(aKey, theParameter);
		}
		
		@Override
		public String toString() {
			return query.toString();
		}

	}
}
