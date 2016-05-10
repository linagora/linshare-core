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
import java.util.List;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.springframework.orm.hibernate3.HibernateTemplate;

abstract class GenericUserRepositoryImpl<U extends User> extends GenericAccountRepositoryImpl<U> implements UserRepository<U> {

	public GenericUserRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public U create(U entity) throws BusinessException {
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		entity.setLsUuid(UUID.randomUUID().toString());
		return super.create(entity);
	}

	/* TODO : check call hierarchy to remove research by mail only. too dangerous. */
	@Override
	public U findByMail(String mail) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("mail", mail).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));
		List<U> users = findByCriteria(criteria);

		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			throw new IllegalStateException("Mail must be unique");
		}
	}

	@Override
	public U findByMailAndDomain(String domainId, String mail) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid",domainId));
		criteria.add(Restrictions.eq("mail", mail).ignoreCase());
		criteria.add(Restrictions.eq("destroyed", 0L));

		List<U> users = findByCriteria(criteria);
		if (users == null || users.isEmpty()) {
			return null;
		} else if (users.size() == 1) {
			return users.get(0);
		} else {
			logger.error("Mail and domain must be unique : " + domainId + " : "
					+ mail);
			throw new IllegalStateException("Mail and domain must be unique");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<U> findByDomain(String domainId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.createAlias("domain", "domain");
		criteria.add(Restrictions.eq("domain.uuid",domainId));
		criteria.add(Restrictions.eq("destroyed", 0L));
		return findByCriteria(criteria);
	}

	@Override
	public List<U> findByCriteria(AccountOccupationCriteriaBean accountCriteria) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("destroyed",0L));
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
			criteria.add(Restrictions.like("domain.uuid", accountCriteria.getActorDomain()).ignoreCase());
		}
		return findByCriteria(criteria);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> findMails(final String beginWith) {
		DetachedCriteria crit = DetachedCriteria.forClass(User.class)
				.add(Restrictions.ilike("mail", beginWith, MatchMode.ANYWHERE))
				.add(Restrictions.eq("destroyed", 0L))
				.setProjection(Projections.property("mail"));

		return listByCriteria(crit);
	}
}
