/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.Validate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.constants.DomainPurgeStepEnum;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.LdapConnection;
import org.linagora.linshare.core.domain.entities.LdapWorkSpaceFilter;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.RootDomain;
import org.linagora.linshare.core.domain.entities.SubDomain;
import org.linagora.linshare.core.domain.entities.TopDomain;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.domain.entities.UserLdapPattern;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate5.HibernateTemplate;

import com.google.common.collect.Maps;

import static org.hibernate.criterion.MatchMode.ANYWHERE;

public class AbstractDomainRepositoryImpl extends
		AbstractRepositoryImpl<AbstractDomain> implements
		AbstractDomainRepository {

	@SuppressWarnings("rawtypes")
	private static HashMap<DomainType, Class> classes = Maps.newHashMap();

	public AbstractDomainRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
		classes.put(DomainType.GUESTDOMAIN, GuestDomain.class);
		classes.put(DomainType.ROOTDOMAIN, RootDomain.class);
		classes.put(DomainType.SUBDOMAIN, SubDomain.class);
		classes.put(DomainType.TOPDOMAIN, TopDomain.class);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AbstractDomain entity) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("uuid", entity.getUuid()));
		return det;
	}

	@Override
	public List<AbstractDomain> findAll() {
		return findByCriteria(
				DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE))
				.addOrder(Order.asc("label"))
			);
	}

	@Override
	public PageContainer<AbstractDomain> findAll(
			Optional<DomainType> domainType,
			Optional<String> name, Optional<String> description,
			Optional<AbstractDomain> parent,
			Optional<AbstractDomain> from,
			SortOrder sortOrder, DomainField sortField,
			PageContainer<AbstractDomain> container) {
		// count matched data
		DetachedCriteria detachedCritCount = getCriteria(domainType, name, description, parent, from);
		detachedCritCount.setProjection(Projections.rowCount());
		Long totalNumberElements = DataAccessUtils.longResult(findByCriteria(detachedCritCount));
		// retrieve one page.
		DetachedCriteria detachedCritData = getCriteria(domainType, name, description, parent, from);
		Order order = null;
		switch (sortField) {
			case name:
				order = SortOrder.ASC.equals(sortOrder) ? Order.asc("label") : Order.desc("label");
				break;
			default:
				order = SortOrder.ASC.equals(sortOrder) ? Order.asc(sortField.toString()) : Order.desc(sortField.toString());
				break;
		}
		detachedCritData.addOrder(order);
		PageContainer<AbstractDomain> res = findAll(detachedCritData, totalNumberElements, container);
		return res;
	}

	private DetachedCriteria getCriteria(
			Optional<DomainType> domainType,
			Optional<String> name, Optional<String> description,
			Optional<AbstractDomain> parent,
			Optional<AbstractDomain> from) {
		DetachedCriteria crit = null;
		if (domainType.isPresent()) {
			crit = DetachedCriteria.forClass(classes.get(domainType.get()));
		} else {
			crit = DetachedCriteria.forClass(getPersistentClass());
		}
		crit.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		if (parent.isPresent()) {
			crit.add(Restrictions.eq("parentDomain", parent.get()));
		}
		if (name.isPresent()) {
			crit.add(Restrictions.ilike("label", name.get(), ANYWHERE));
		}
		if (description.isPresent()) {
			crit.add(Restrictions.ilike("description", description.get(), ANYWHERE));
		}
		if (from.isPresent()) {
			// nested administrators must only see their domain and their nested domains.
			crit.add(
				Restrictions.or(
					Restrictions.eq("persistenceId", from.get().getPersistenceId()),
					Restrictions.eq("parentDomain", from.get())
				)
			);
		}
		return crit;
	}

	@Override
	public AbstractDomain findById(String identifier) {
		return DataAccessUtils.singleResult(findByCriteria(
				Restrictions.eq("uuid", identifier)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllDomainIdentifiers() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"))
				.addOrder(Order.asc("authShowOrder"));
		crit.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		return listByCriteria(crit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllDomainIdentifiersForAuthenticationDiscovery() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"))
				.addOrder(Order.desc("creationDate"));
		crit.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		return listByCriteria(crit);
	}

	@Override
	public List<AbstractDomain> findAllDomain() {
		return findByCriteria(DetachedCriteria.forClass(getPersistentClass())
				.addOrder(Order.asc("authShowOrder"))
				.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AbstractDomain> findAllTopAndSubDomain() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.add(Restrictions.disjunction()
				.add(Restrictions.eq("class", TopDomain.class))
				.add(Restrictions.eq("class", SubDomain.class)))
			.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		return listByCriteria(crit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllGuestAndSubDomainIdentifiers() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass());
		crit.setProjection(Projections.property("uuid"));
		crit.add(Restrictions.disjunction()
				.add(Restrictions.eq("class", GuestDomain.class))
				.add(Restrictions.eq("class", SubDomain.class)))
			.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		return listByCriteria(crit);
	}

	@Override
	public List<AbstractDomain> findAllTopDomain() {
		return findByCriteria(DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("class", TopDomain.class))
				.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE)));
	}

	@Override
	public List<AbstractDomain> findAllSubDomain() {
		return findByCriteria(DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("class", SubDomain.class))
				.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE)));
	}

	@Override
	public List<AbstractDomain> findByCurrentMailConfig(MailConfig cfg) {
		return findByCriteria(DetachedCriteria.forClass(getPersistentClass())
				.add(Restrictions.eq("currentMailConfiguration", cfg))
				.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE)));
	}

	@Override
	public AbstractDomain getUniqueRootDomain() throws BusinessException {
		AbstractDomain domain = this.findById(LinShareConstants.rootDomainIdentifier);
		if (domain == null) {
			throw new BusinessException(
					BusinessErrorCode.DATABASE_INCOHERENCE_NO_ROOT_DOMAIN,
					"No root domain found in the database.");
		}
		return domain;
	}

	@Override
	public List<AbstractDomain> loadDomainsForAWelcomeMessage(
			WelcomeMessages welcomeMessage) throws BusinessException {
		return findByCriteria(Restrictions.eq("currentWelcomeMessage",
				welcomeMessage));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllSubDomainIdentifiers(String domain) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.createAlias("parentDomain", "parent");
		det.add(Restrictions.eq("parent.uuid", domain));
		det.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		det.setProjection(Projections.property("uuid"));
		List<String> ret = listByCriteria(det);
		return ret;
	}

	@Override
	public AbstractDomain create(AbstractDomain entity) throws BusinessException {
		entity.setUuid(UUID.randomUUID().toString());
		entity.setCreationDate(new Date());
		entity.setModificationDate(new Date());
		return super.create(entity);
	}

	@Override
	public AbstractDomain update(AbstractDomain entity) throws BusinessException {
		entity.setModificationDate(new Date());
		return super.update(entity);
	}

	@Override
	public void markToPurge(AbstractDomain abstractDomain) throws BusinessException, IllegalArgumentException {
		abstractDomain.setPurgeStep(DomainPurgeStepEnum.WAIT_FOR_PURGE);
		abstractDomain.setModificationDate(new Date());
		this.update(abstractDomain);
	}

	@Override
	public void purge(AbstractDomain abstractDomain) throws BusinessException, IllegalArgumentException {
		abstractDomain.setPurgeStep(DomainPurgeStepEnum.PURGED);
		abstractDomain.setModificationDate(new Date());
		this.update(abstractDomain);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllAbstractDomainsReadyToPurge() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.setProjection(Projections.property("uuid"));
		criteria.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.WAIT_FOR_PURGE));
		return listByCriteria(criteria);
	}

	@Override
	public AbstractDomain findDomainReadyToPurge(String lsUuid) {
		Validate.notNull(lsUuid);
		DetachedCriteria criteria = DetachedCriteria.forClass(getPersistentClass());
		criteria.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.WAIT_FOR_PURGE));
		criteria.add(Restrictions.eq("uuid", lsUuid).ignoreCase());
		return DataAccessUtils.requiredSingleResult(findByCriteria(criteria));
	}
	
	@Override
	public List<AbstractDomain> getSubDomainsByDomain(String domain) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.createAlias("parentDomain", "parent");
		det.add(Restrictions.eq("parent.uuid", domain));
		det.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		det.addOrder(Order.asc("label"));
		return findByCriteria(det);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getSubDomainsByDomainIdentifiers(String domain) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"));
		det.createAlias("parentDomain", "parent");
		det.add(Restrictions.eq("parent.uuid", domain));
		det.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		return listByCriteria(det);
	}

	@Override
	public AbstractDomain getGuestSubDomainByDomain(String uuid) {
		DetachedCriteria det = DetachedCriteria.forClass(GuestDomain.class);
		det.createAlias("parentDomain", "parent");
		det.add(Restrictions.eq("parent.uuid", uuid));
		det.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		return DataAccessUtils.singleResult(findByCriteria(det));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllDomainIdentifiersWithGroupProviders() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"))
				.addOrder(Order.asc("authShowOrder"));
		crit.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		crit.add(Restrictions.isNotNull("groupProvider"));
		return listByCriteria(crit);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> findAllDomainIdentifiersWithWorkSpaceProviders() {
		DetachedCriteria crit = DetachedCriteria.forClass(getPersistentClass())
				.setProjection(Projections.property("uuid"))
				.addOrder(Order.asc("authShowOrder"));
		crit.add(Restrictions.eq("purgeStep", DomainPurgeStepEnum.IN_USE));
		crit.add(Restrictions.isNotNull("workSpaceProvider"));
		return listByCriteria(crit);
	}

	@Override
	public List<AbstractDomain> findAllDomainsByLdapConnection(LdapConnection ldapConnection) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass(), "abstractDomain");
		det.createAlias("abstractDomain.userProvider", "userProvider");
		det.add(Restrictions.eq("userProvider.ldapConnection", ldapConnection));
		return findByCriteria(det);
	}

	@Override
	public List<AbstractDomain> findAllDomainsByTwakeConnection(TwakeConnection twakeConnection) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass(), "abstractDomain");
		det.createAlias("abstractDomain.userProvider", "userProvider");
		det.add(Restrictions.eq("userProvider.twakeConnection", twakeConnection));
		return findByCriteria(det);
	}

	@Override
	public List<AbstractDomain> findAllDomainsByUserFilter(UserLdapPattern domainUserFilter) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass(), "abstractDomain");
		det.createAlias("abstractDomain.userProvider", "userProvider");
		det.add(Restrictions.eq("userProvider.pattern", domainUserFilter));
		return findByCriteria(det);
	}

	@Override
	public List<AbstractDomain> findAllDomainsByGroupFilter(GroupLdapPattern domainGroupFilter) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass(), "abstractDomain");
		det.createAlias("abstractDomain.groupProvider", "groupProvider");
		det.add(Restrictions.eq("groupProvider.groupPattern", domainGroupFilter));
		return findByCriteria(det);
	}

	@Override
	public List<AbstractDomain> findAllDomainsByWorkSpaceFilter(LdapWorkSpaceFilter domainGroupFilter) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass(), "abstractDomain");
		det.createAlias("abstractDomain.workSpaceProvider", "workSpaceProvider");
		det.add(Restrictions.eq("workSpaceProvider.workSpaceFilter", domainGroupFilter));
		return findByCriteria(det);
	}
}
