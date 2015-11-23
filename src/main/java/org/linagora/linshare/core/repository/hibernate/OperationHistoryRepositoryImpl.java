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
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.OperationHistoryRepository;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class OperationHistoryRepositoryImpl extends AbstractRepositoryImpl<OperationHistory>
		implements OperationHistoryRepository {

	public OperationHistoryRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public OperationHistory create(OperationHistory entity) throws BusinessException {
		entity.setCreationDate(new Date());
		return super.create(entity);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(OperationHistory entity) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		return criteria.add(Restrictions.eq("id", entity.getId()));
	}

	@Override
	public List<Account> findAccountBeforeDate(Date Date, EnsembleType ensembleType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if(ensembleType != null){
			criteria.add(Restrictions.eq("ensembleType", ensembleType));
		}
		criteria.add(Restrictions.le("creationDate", Date));
		criteria.setProjection(Projections.property("account"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<Account> result = getHibernateTemplate().findByCriteria(criteria);
		return result;
	}

	@Override
	public Long sumOperationValue(Account account, AbstractDomain domain, Date creationDate, OperationHistoryTypeEnum operationType, EnsembleType ensembleType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if(account != null){
			criteria.add(Restrictions.eq("account", account));
		}
		if(domain != null){
			criteria.add(Restrictions.eq("domain", domain));
		}
		if(operationType != null){
			criteria.add(Restrictions.eq("operationType", operationType));
		}
		if(creationDate != null){
			criteria.add(Restrictions.le("creationDate", creationDate));
		}
		if(ensembleType != null){
			criteria.add(Restrictions.eq("ensembleType", ensembleType));
		}
		criteria.setProjection(Projections.sum("operationValue"));
		List<OperationHistory> list = findByCriteria(criteria);
		if (list.size() > 0 && list.get(0) != null)
			return DataAccessUtils.longResult(findByCriteria(criteria));
		return (long) 0;
	}

	@Override
	public Long countOperationValue(Account account, AbstractDomain domain, Date creationDate, OperationHistoryTypeEnum operationType, EnsembleType ensembleType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if(account != null){
			criteria.add(Restrictions.eq("account", account));
		}
		if(domain != null){
			criteria.add(Restrictions.eq("domain", domain));
		}
		if(creationDate != null){
			criteria.add(Restrictions.le("creationDate", creationDate));
		}
		if(operationType != null){
			criteria.add(Restrictions.eq("operationType", operationType));
		}
		if(ensembleType != null){
			criteria.add(Restrictions.eq("ensembleType", ensembleType));
		}
		criteria.setProjection(Projections.rowCount());
		return DataAccessUtils.longResult(findByCriteria(criteria));
	}

	@Override
	public List<AbstractDomain> findDomainBeforeDate(Date creationDate) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.lt("creationDate", creationDate));
		criteria.setProjection(Projections.property("domain"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<AbstractDomain> result = getHibernateTemplate().findByCriteria(criteria);
		return result;
	}


	@Override
	public void deleteBeforeDate(Date creationDate) {
		List<OperationHistory> list = find(null, null, null, creationDate);
		for (OperationHistory entity : list) {
			super.delete(entity);
		}
	}

	@Override
	public List<OperationHistory> find(Account account, AbstractDomain domain, EnsembleType ensembleType, Date date) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if(account != null){
			criteria.add(Restrictions.eq("account", account));
		}

		if(ensembleType != null){
			criteria.add(Restrictions.eq("ensembleType", ensembleType));
		}

		if(domain != null){
			criteria.add(Restrictions.eq("domain", domain));
		}

		if(date != null){
			criteria.add(Restrictions.le("creationDate", date));
		}
		return findByCriteria(criteria);
	}

	@Override
	public List<String> findUuidAccountBeforeDate(Date date, EnsembleType ensembleType) {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		if(ensembleType != null){
			criteria.add(Restrictions.eq("ensembleType", ensembleType));
		}
		criteria.add(Restrictions.le("creationDate", date));
		criteria.setProjection(Projections.property("account"));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		@SuppressWarnings("unchecked")
		List<Account> listAccount = getHibernateTemplate().findByCriteria(criteria);

		List<String> listUuid = new ArrayList<>();
		for(Account account : listAccount){
			listUuid.add(account.getLsUuid());
		}
		return listUuid;
	}

	@Override
	public void deleteBeforeDateByAccount(Date date, Account account) {
		List<OperationHistory> list = find(account, null, null, date);
		for (OperationHistory entity : list) {
			super.delete(entity);
		}
	}
}
