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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GenericStatistic;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.GenericStatisticRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

// TODO FIXME Quota & Statistics
public abstract class GenericStatisticRepositoryImpl<T extends GenericStatistic> extends AbstractRepositoryImpl<T>
		implements GenericStatisticRepository<T> {

	public GenericStatisticRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public void deleteBeforeDate(Date date) {
		// TODO FIXME Quota & Statistics : Very ugly.
		List<T> list = findBetweenTwoDates(null, null, null, null, date, null);
		for (T entity : list) {
			delete(entity);
		}
	}

	@Override
	public Long sumOfOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("operationCount"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long sumOfDeleteOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("deleteOperationCount"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long sumOfCreateOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("createOperationCount"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long sumOfCreateOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("createOperationSum"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long sumOfDeleteOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("deleteOperationSum"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long sumOfDiffOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("diffOperationSum"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long sumOfActualOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.between("statisticDate", beginDate, endDate));
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		criteria.setProjection(Projections.sum("actualOperationSum"));
		List<T> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public List<T> findBetweenTwoDates(Account account, AbstractDomain domain, AbstractDomain parentDomain,
			Date beginDate, Date endDate, StatisticType statisticType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null && endDate != null) {
			if (beginDate.after(endDate)) {
				throw new BusinessException("Begin date can be after end date.");
			}
		}
		if (beginDate != null) {
			criteria.add(Restrictions.ge("statisticDate", beginDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("statisticDate", endDate));
		}
		if (account != null) {
			criteria.add(Restrictions.eq("account", account));
		}
		if (domain != null) {
			criteria.add(Restrictions.eq("domain", domain));
		}
		if (parentDomain != null) {
			criteria.add(Restrictions.eq("parentDomain", parentDomain));
		}
		if (statisticType != null) {
			criteria.add(Restrictions.eq("statisticType", statisticType));
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<AbstractDomain> findDomainBetweenTwoDates(Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null) {
			criteria.add(Restrictions.ge("statisticDate", beginDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("statisticDate", endDate));
		}
		criteria.setProjection(Projections.property("domain"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<AbstractDomain> listDomains = (List<AbstractDomain>) getHibernateTemplate().findByCriteria(criteria);
		return  listDomains;
	}

	@Override
	public List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null) {
			criteria.add(Restrictions.ge("statisticDate", beginDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("statisticDate", endDate));
		}
		criteria.setProjection(Projections.property("account"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<Account> listAccounts = (List<Account>) getHibernateTemplate().findByCriteria(criteria);
		return listAccounts;
	}

	@Override
	public DetachedCriteria getNaturalKeyCriteria(T entity) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		return criteria.add(Restrictions.eq("id", entity.getId()));
	}

	@Override
	public T create(T entity) throws BusinessException{
		entity.setCreationDate(new Date());
		return super.create(entity);
	}

	@Override
	public List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null) {
			criteria.add(Restrictions.ge("statisticDate", beginDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("statisticDate", endDate));
		}
		criteria.setProjection(Projections.property("account"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<Account> listAccount = (List<Account>) getHibernateTemplate().findByCriteria(criteria);

		List<String> listUuid = new ArrayList<>();
		for (Account acccount : listAccount) {
			listUuid.add(acccount.getLsUuid());
		}
		return listUuid;
	}

	@Override
	public List<String> findIdentifierDomainBetweenTwoDates(Date beginDate, Date endDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if (beginDate != null) {
			criteria.add(Restrictions.ge("statisticDate", beginDate));
		}
		if (endDate != null) {
			criteria.add(Restrictions.le("statisticDate", endDate));
		}
		criteria.setProjection(Projections.property("domain"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<AbstractDomain> listDomain = (List<AbstractDomain>) getHibernateTemplate().findByCriteria(criteria);

		List<String> identifiers = new ArrayList<>();
		for (AbstractDomain domain : listDomain) {
			identifiers.add(domain.getUuid());
		}
		return identifiers;
	}
}
