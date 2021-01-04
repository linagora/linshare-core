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

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.view.tapestry.beans.AccountOccupationCriteriaBean;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.base.Strings;

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

	@Override
	public PageContainer<U> findAll(AbstractDomain domain, String creationDate, String modificationDate, String mail,
			String firstName, String lastName, Boolean restricted, Boolean canCreateGuest, Boolean canUpload, Role role,
			AccountType type, PageContainer<U> container) {
		DetachedCriteria detachedCrit = getAllCriteria(domain);
		if (!Strings.isNullOrEmpty(mail)) {
			detachedCrit.add(Restrictions.ilike("mail", mail, MatchMode.ANYWHERE));
		}
		if (!Strings.isNullOrEmpty(firstName)) {
			detachedCrit.add(Restrictions.ilike("firstName", firstName, MatchMode.ANYWHERE));
		}
		if (!Strings.isNullOrEmpty(lastName)) {
			detachedCrit.add(Restrictions.ilike("lastName", lastName, MatchMode.ANYWHERE));
		}
		if (Objects.nonNull(restricted)) {
			detachedCrit.add(Restrictions.eq("restricted", restricted));
		}
		if (Objects.nonNull(canCreateGuest)) {
			detachedCrit.add(Restrictions.eq("canCreateGuest", canCreateGuest));
		}
		if (Objects.nonNull(canUpload)) {
			detachedCrit.add(Restrictions.eq("canUpload", canUpload));
		}
		if (Objects.nonNull(role)) {
			detachedCrit.add(Restrictions.eq("role", role));
		}
		if (Objects.nonNull(type)) {
			detachedCrit.add(Restrictions.in("class", type.toInt()));
		}
		detachedCrit.addOrder(Order.desc("modificationDate"));
		return findAll(detachedCrit, count(domain), container);
	}

	private Long count(AbstractDomain domain) {
		DetachedCriteria detachedCrit = getAllCriteria(domain);
		detachedCrit.setProjection(Projections.rowCount());
		return (Long) detachedCrit.getExecutableCriteria(getCurrentSession()).uniqueResult();
	}

	private DetachedCriteria getAllCriteria(AbstractDomain domain) {
		DetachedCriteria detachedCrit = DetachedCriteria.forClass(getPersistentClass());
		detachedCrit.add(Restrictions.eq("destroyed", 0L));
		if(!Objects.isNull(domain)) {
			detachedCrit.add(Restrictions.eq("domain", domain));
		}
		return detachedCrit;
	}
}
