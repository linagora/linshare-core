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
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class RecipientFavouriteRepositoryImpl extends AbstractRepositoryImpl<RecipientFavourite> implements RecipientFavouriteRepository {

	public RecipientFavouriteRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public boolean existFavourite(User owner, String element) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.eq("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);


		return (listElement!=null && !listElement.isEmpty());	

	}

	@Override
	public String getElementWithMaxWeight(User u) throws LinShareNotSuchElementException {

		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.addOrder(Order.desc("weight"));
		List<RecipientFavourite> listElement=findByCriteria(det);


		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}

		return listElement.get(0).getRecipient();
	}

	@Override
	public List<String> getElementsOrderByWeight(User u) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.addOrder(Order.asc("weight"));
		List<RecipientFavourite> listElement=findByCriteria(det);

		return transformRecipientFavouriteToRecipient(listElement);
	}

	@Override
	public List<String> getElementsOrderByWeightDesc(User u) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.addOrder(Order.desc("weight"));

		List<RecipientFavourite> listElement=findByCriteria(det);

		return transformRecipientFavouriteToRecipient(listElement);
	}

	@Override
	public Long getWeight(String element,User u)  throws LinShareNotSuchElementException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.eq("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);
		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}
		return listElement.get(0).getWeight();
	}

	@Override
	public void incAndCreate(User u, String mail) throws BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.eq("recipient", mail));
		RecipientFavourite recipient = DataAccessUtils
				.singleResult(findByCriteria(det));
		if (recipient == null) {
			recipient = create(new RecipientFavourite(u, mail));
		} else {
			recipient.inc();
		}
		update(recipient);
	}

	@Override
	public void inc(List<String> element,User u)  throws LinShareNotSuchElementException,BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.in("recipient", element));
		List<RecipientFavourite> listElement=findByCriteria(det);
		if(listElement==null || listElement.isEmpty() ){
			throw new LinShareNotSuchElementException("the owner has no recipient associated ");
		}
		if(listElement.size()!=element.size()){
			throw new LinShareNotSuchElementException("one of the recipient is not present !");
		}
		for(RecipientFavourite recipientFavourite: listElement){
			recipientFavourite.inc();
			update(recipientFavourite);
		}

	}

	@Override
	public void incAndCreate(User u, List<String> elements)   throws LinShareNotSuchElementException,BusinessException{
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", u));
		det.add(Restrictions.in("recipient", elements));

		List<RecipientFavourite> recipients=findByCriteria(det);

		/**
		 * Create favourite when the favourite doesn't exist in the database for the current owner.
		 */
		createFavourite(elements, recipients, u);

		/**
		 * Increment and update others.
		 */
		
		
		for(RecipientFavourite recipientFavourite: recipients){
			recipientFavourite.inc();
			update(recipientFavourite);
		}
	}

	private List<String> transformRecipientFavouriteToRecipient(List<RecipientFavourite> recipients){
		ArrayList<String> listElements=new ArrayList<String>();
		if(null!=recipients){
			for(RecipientFavourite recipientFavourite: recipients){
				listElements.add(recipientFavourite.getRecipient());
			}
		}
		return listElements;
	}

	private void createFavourite(List<String> elements,List<RecipientFavourite> recipients,User u) throws BusinessException{
		for(String recipient:elements){
			boolean contain=false;
			for(RecipientFavourite recipientFavour: recipients){
				if(recipientFavour.getRecipient().equals(recipient)){
					contain=true;
					break;
				}
			}
			if(!contain){
				super.create(new RecipientFavourite(u,recipient));
			}
		}
	}
	
	private List<String> reorderElement(List<String> elements,List<RecipientFavourite> recipients){
		
		ArrayList<String> orderedElements=new ArrayList<String>();

		//1) first put favorite at the top of the list
		for(RecipientFavourite recipientFavour: recipients){
			orderedElements.add(recipientFavour.getRecipient());
		}
		
		//2) second put remaining entries in the list (exclude them if they are present in favorite)
		for(String element:elements){
			if(!orderedElements.contains(element)){
				orderedElements.add(element);
			}
		}
		return orderedElements;
	}
	
	@Override
	protected DetachedCriteria getNaturalKeyCriteria(RecipientFavourite entity) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class).add(
				Restrictions.eq("owner", entity.getOwner())).add(
						Restrictions.eq("recipient", entity.getRecipient()));
		return det;
	}

	@Override
	public List<String> reorderElementsByWeightDesc(List<String> elements, User owner)
			{
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.in("recipient", elements));
		det.addOrder(Order.desc("weight"));
		List<RecipientFavourite> recipients=findByCriteria(det);
		
		return reorderElement(elements, recipients);
		
	}

	@Override
	public List<String> findMatchElementsOrderByWeight(String matchStartWith, User owner) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.ilike("recipient", matchStartWith,MatchMode.ANYWHERE));
		det.addOrder(Order.desc("weight"));
		List<RecipientFavourite> recipients = findByCriteria(det);
		ArrayList<String> mails=new ArrayList<String>();
		for(RecipientFavourite recipientFavour: recipients){
			mails.add(recipientFavour.getRecipient());
		}
		return mails;
	}

	@Override
	public List<RecipientFavourite> findMatchElementsOrderByWeight(
			String matchStartWith, User owner, int limit) {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		det.add(Restrictions.ilike("recipient", matchStartWith,MatchMode.ANYWHERE));
		det.addOrder(Order.desc("weight"));
		return findByCriteria(det, limit);
	}

	@Override
	public void deleteFavoritesOfUser(User owner) throws IllegalArgumentException, BusinessException {
		DetachedCriteria det = DetachedCriteria.forClass(RecipientFavourite.class);
		det.add(Restrictions.eq("owner", owner));
		List<RecipientFavourite> recipients=findByCriteria(det);
		for (RecipientFavourite recipientFavourite : recipients) {
			delete(recipientFavourite);
		}
	}

	@Override
	public void updateEmail(final String currentEmail, final String newEmail)
			throws BusinessException {
		HibernateCallback<Long> action = new HibernateCallback<Long>() {
			public Long doInHibernate(final Session session)
					throws HibernateException, SQLException {
				final Query query = session.createQuery(
						"UPDATE RecipientFavourite SET recipient_mail = :newEmail WHERE recipient_mail = :currentEmail");
				query.setParameter("newEmail", newEmail);
				query.setParameter("currentEmail", currentEmail);
				long updatedCounter = (long) query.executeUpdate();
				logger.info(updatedCounter
						+ " RecipientFavourite have been updated.");
				return updatedCounter;
			}
		};
		getHibernateTemplate().execute(action);
	}
}
