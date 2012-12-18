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
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

abstract class GenericUserRepositoryImpl<U extends User> extends GenericAccountRepositoryImpl<U> implements UserRepository<U> {

	public GenericUserRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}
	

	@Override
	public U findByMail(String mail) {
		 List<U> users = findByCriteria(Restrictions.eq("mail", mail).ignoreCase());
	        if (users == null || users.isEmpty()) {
	            return null;
	        } else if (users.size() == 1) {
	            return users.get(0);
	        } else {
	            throw new IllegalStateException("Mail must be unique");
	        }
	}
	
	@Override
	public U findByMailAndDomain(String domain, String mail) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.like("domain.identifier",domain));
		criteria.add(Restrictions.eq("mail", mail).ignoreCase());
		
		 List<U> users = findByCriteria(criteria);
		 
        if (users == null || users.isEmpty()) {
            return null;
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            throw new IllegalStateException("Mail and domain must be unique");
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<U> findByDomain(String domain) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.like("domain.identifier",domain));
		
		return getHibernateTemplate().findByCriteria(criteria);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<U> findByCriteria(AccountOccupationCriteriaBean accountCriteria) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		
		if ((accountCriteria.getActorMails()!=null) && (accountCriteria.getActorMails().size()>0)) {
			criteria.add(Restrictions.in("mail", accountCriteria.getActorMails()));
		}
		
		if ((accountCriteria.getActorFirstname()!=null) && (accountCriteria.getActorFirstname().length()>0)) {
			criteria.add(Restrictions.like("firstName", accountCriteria.getActorFirstname(), MatchMode.START).ignoreCase());
		}
		
		if ((accountCriteria.getActorLastname()!=null) && (accountCriteria.getActorLastname().length()>0)) {
			criteria.add(Restrictions.like("lastName", accountCriteria.getActorLastname(), MatchMode.START).ignoreCase());
		}
		
		if ((accountCriteria.getActorDomain()!=null) && (accountCriteria.getActorDomain().length()>0)) {
			criteria.createAlias("domain", "domain");
			criteria.add(Restrictions.like("domain.identifier", accountCriteria.getActorDomain()).ignoreCase());
		}
		
		
		return getHibernateTemplate().findByCriteria(criteria);
	}
	
	
//
//	public void removeOwnerDocumentForUser(String login, String uuid)
//			throws BusinessException {
//		U user=findByLogin(login);
//		
//		Set<Document> documents=user.getDocuments();
//				
//		Document documentTemp=null;
//		for(Document document:documents){
//			if(document.getIdentifier().equals(uuid)){
//				documentTemp=document;
//				break;
//			}
//		}
//		user.deleteDocument(documentTemp);
//		
//		update(user);
//
//	}
//
//	public void removeReceivedDocument(String login, String uuid)
//			throws BusinessException {
//		U user=findByLogin(login);
//		
//		List<Share> shares=new ArrayList<Share>(user.getReceivedShares());
//				
//		for(Share currentShare:shares){
//			if(currentShare.getDocument().getIdentifier().equals(uuid)){
//				user.deleteReceivedShare(currentShare);
//				break;
//			}
//		}
//		
//		update(user);
//
//	}
//
//	public void removeSentDocument(String login, String uuid)
//			throws BusinessException {
//		
//		U user=findByLogin(login);
//		
//		List<Share> shares=new ArrayList<Share>(user.getShares());
//				
//		for(Share currentShare:shares){
//			if(currentShare.getDocument().getIdentifier().equals(uuid)){
//				user.deleteShare(currentShare);
//			}
//		}
//		
//		update(user);
//
//	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findMails(final String beginWith) {
		
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(final Session session)
			throws HibernateException, SQLException {
				
						
				final Query query = session.createQuery("select u.mail from User u where lower(u.mail) like :mail");
				
				
				query.setParameter("mail", beginWith+"%");
				
				
				
				return query.setCacheable(true).list();
			}
		});
				
	}
}
