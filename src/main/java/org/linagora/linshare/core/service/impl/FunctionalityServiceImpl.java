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

package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.mongo.entities.logs.FunctionalityAuditLogEntry;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FunctionalityServiceImpl extends AbstractFunctionalityServiceImpl<Functionality> implements FunctionalityService {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityServiceImpl.class);

	final private FunctionalityBusinessService businessService;

	protected List<String> excludesForUsers = new ArrayList<String>();

	final private AuditAdminMongoRepository mongoRepository;

	public FunctionalityServiceImpl(
			FunctionalityBusinessService functionalityBusinessService,
			DomainBusinessService domainBusinessService,
			DomainPermissionBusinessService domainPermissionBusinessService,
			AuditAdminMongoRepository mongoRepository) {
		super(domainBusinessService, domainPermissionBusinessService);
		this.businessService = functionalityBusinessService;
		this.mongoRepository = mongoRepository;
		// Users
		excludesForUsers.add(FunctionalityNames.SHARE_NOTIFICATION_BEFORE_EXPIRATION.toString());
		excludesForUsers.add(FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION.toString());
		excludesForUsers.add(FunctionalityNames.DOMAIN__MAIL.toString());
		excludesForUsers.add(FunctionalityNames.ANTIVIRUS.toString());
		excludesForUsers.add(FunctionalityNames.TIME_STAMPING.toString());
	}

	@Override
	public Iterable<Functionality> findAll(Account actor, String domainId) throws BusinessException {
		return findAll(actor, domainId, null, false, false);
	}

	@Override
	public Iterable<Functionality> findAll(Account actor, String domainId,
			String parentId) throws BusinessException {
		return findAll(actor, domainId, parentId, false, false);
	}

	@Override
	public Iterable<Functionality> findAll(Account actor, String domainId,
			String parentId, boolean tree, boolean withSubFunctionalities)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		Validate.notEmpty(domainId);
		FunctionalityNames parentIdentifier = null;
		if (parentId != null) {
			// check if it is a valid parent identifier
			parentIdentifier = FunctionalityNames.valueOf(parentId);
		}
		AbstractDomain domain = getDomain(actor, domainId);
		Set<Functionality> functionalities = businessService.getAllFunctionalities(domain, excludes);
		Map<String, Functionality> parents = Maps.newHashMap();
		List<Functionality> subs = Lists.newArrayList();
		for (Functionality f : functionalities) {
			// We check if this a sub functionality (a parameter)
			if (f.isParam()) {
				subs.add(f);
			} else {
				parents.put(f.getIdentifier(), f);
			}
		}

		Map<String, List<Functionality>> children = new HashMap<String, List<Functionality>>();
		for (Functionality func : subs) {
			if (parents.containsKey(func.getParentIdentifier())) {
				// if parent contains at least one sub functionality to display, the parent must be display.
				if (func.isDisplayable()) {
					Functionality parent = parents.get(func.getParentIdentifier());
					// We need to display the parent functionality to display children
					// only if the parent is not forbidden.
					if (!parent.getActivationPolicy().isForbidden()) {
						parent.setDisplayable(true);
					}
				}
				if (tree) {
					parents.get(func.getParentIdentifier()).addChild(func);
				}
				// storing children in maps
				List<Functionality> list = children.get(func.getParentIdentifier());
				if (list == null) {
					list = Lists.newArrayList();
					children.put(func.getParentIdentifier(), list);
				}
				list.add(func);
			} else {
				logger.error("Sub functionality {} without a parent : {}", func.getIdentifier(), func.getParentIdentifier());
			}
		}
		List<Functionality> result = Lists.newArrayList();
		if (parentId == null) {
			result.addAll(parents.values());
		} else {
			List<Functionality> list = children.get(parentIdentifier.toString());
			if (list == null) {
				logger.debug("Functionality {} has no children.", parentIdentifier.toString());
				return Lists.newArrayList();
			}
			return list;
		}
		if (withSubFunctionalities) {
			result.addAll(subs);
		}
		return Iterables.filter(result, isDisplayable());
	}

	@Override
	public Functionality find(Account actor, String domainId, String functionalityId, boolean tree) throws BusinessException {
		Validate.notEmpty(domainId);
		Validate.notEmpty(functionalityId);
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		logger.debug("looking for functionality : " + functionalityId + " in domain "+ domainId);
		AbstractDomain domain = getDomain(actor, domainId);
		Functionality entity = businessService.getFunctionality(domain, functionalityId);
		Set<Functionality> functionalities = businessService.getAllFunctionalities(domain, excludes);
		for (Functionality f : functionalities) {
			if (f.isParam()) {
				if (f.getParentIdentifier().equals(functionalityId)) {
					// We check if children should by display
					if (f.isDisplayable()) {
						entity.setDisplayable(true);
						if (tree) {
							entity.addChild(f);
						}
					}
				}
			}
		}
		if (!entity.isDisplayable()) {
			throw new BusinessException(BusinessErrorCode.FUNCTIONALITY_NOT_FOUND, "Functionality not found : " + functionalityId);
		}
		return entity;
	}

	@Override
	public Functionality find(Account actor, String domainId, String identifier)
			throws BusinessException {
		return find(actor, domainId, identifier, false);
	}

	@Override
	public void delete(Account actor, String domainId, String functionalityId) throws IllegalArgumentException, BusinessException {
		Validate.notNull(actor);
		Validate.notEmpty(domainId);
		Validate.notEmpty(functionalityId);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
		Functionality func = find(actor, domainId, functionalityId);
		AbstractDomain domain = getDomain(actor, domainId);
		checkDeleteRights(domain);
		businessService.delete(domainId, functionalityId);
		FunctionalityAuditLogEntry log = new FunctionalityAuditLogEntry(actor, LogAction.DELETE,
				AuditLogEntryType.FUNCTIONALITY, func);
		mongoRepository.insert(log);
	}

	@Override
	public Functionality update(Account actor, String domainId, Functionality functionality) throws BusinessException {
		Validate.notEmpty(domainId);
		Validate.notNull(functionality);
		Validate.notEmpty(functionality.getIdentifier());
		Validate.notNull(actor);
		Validate.isTrue(actor.hasAdminRole() || actor.hasSuperAdminRole());
//		AKO : Some refactoring must be made here, because there actually 3  database calls made by this function
//			We have to make only one call find it first, then the business must returns the functionality.
		AbstractDomain domain = getDomain(actor, domainId);
		if (checkUpdateRights(actor, domain, functionality)) {
			businessService.update(domainId, functionality);
		}
		Functionality func = businessService.getFunctionality(domain, functionality.getIdentifier());
		FunctionalityAuditLogEntry log = new FunctionalityAuditLogEntry(actor, LogAction.UPDATE,
				AuditLogEntryType.FUNCTIONALITY, func);
		mongoRepository.insert(log);
		return func;
	}

	@Override
	public Set<Functionality> findAll(Account actor) throws BusinessException {
		Validate.notNull(actor);
		Set<Functionality> functionalities = businessService
				.getAllFunctionalities(actor.getDomain(), excludesForUsers);
		return functionalities;
	}

	@Override
	public Functionality find(Account actor, String functionalityId)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notEmpty(functionalityId);
		Validate.isTrue(actor.hasSimpleRole());
		Functionality functionality = businessService.getFunctionality(
				actor.getDomain(), functionalityId);
		return functionality;
	}

	private Predicate<Functionality> isDisplayable() {
		return new Predicate<Functionality>() {
			@Override
			public boolean apply(Functionality input) {
				if(input.isDisplayable()){
					return true;
				}
				logger.debug("Functionality filtered: " + input.getIdentifier());
				return false;
			}
		};
	}
}
