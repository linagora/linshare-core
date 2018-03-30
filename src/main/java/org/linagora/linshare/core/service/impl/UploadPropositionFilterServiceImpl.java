/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionFilterBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadPropositionAction;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilterOLD;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionFilterServiceImpl
		implements UploadPropositionFilterService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFilterServiceImpl.class);

	private final UploadPropositionFilterBusinessService businessService;

	private final DomainPermissionBusinessService permissionService;

	public UploadPropositionFilterServiceImpl(final UploadPropositionFilterBusinessService businessService,
			final DomainPermissionBusinessService permissionService) {
		super();
		this.businessService = businessService;
		this.permissionService = permissionService;
	}

	@Override
	public UploadPropositionFilterOLD findOLD(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "filter uuid is required");
		UploadPropositionFilterOLD filter = businessService.findOLD(uuid);
		if (filter ==null) {
			logger.error(actor.getAccountRepresentation() + " is looking for missing filter uuid : " + uuid);
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_NOT_FOUND, "filter with uuid : " + uuid + " not found.");
		}
		return filter;
	}

	@Override
	public UploadPropositionFilter find(Account authUser, AbstractDomain domain, String uuid) throws BusinessException {
		preChecks(authUser);
		Validate.notEmpty(uuid, "filter uuid is required");
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_CAN_NOT_READ,
					"You are not allowed to use this domain");
		}
		UploadPropositionFilter filter = businessService.find(domain.getUuid(), uuid);
		if (filter == null) {
			logger.error(authUser.getAccountRepresentation() + " is looking for missing filter uuid : " + uuid);
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_NOT_FOUND,
					"filter with uuid : " + uuid + " not found.");
		}
		return filter;
	}

	@Override
	public List<UploadPropositionFilter> findAll(Account authUser, AbstractDomain domain) throws BusinessException {
		preChecks(authUser);
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_CAN_NOT_READ,
					"You are not allowed to use this domain");
		}
		return businessService.findAll();
	}

	@Override
	public List<UploadPropositionFilterOLD> findAllEnabledFilters(Account actor) throws BusinessException {
		preChecks(actor);
		if (actor.hasSuperAdminRole() || actor.hasUploadPropositionRole()) {
			return businessService.findAllEnabledFilters();
		} else {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get these filters.");
		}
	}

	@Override
	public UploadPropositionFilter create(User authUser, UploadPropositionFilter uploadPropositionFilter,
			AbstractDomain domain) throws BusinessException {
		preChecks(authUser);
		validateEntryValues(uploadPropositionFilter);
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_CAN_NOT_CREATE,
					"You are not allowed to use this domain");
		}
		UploadPropositionFilter createdFilter = new UploadPropositionFilter(UUID.randomUUID().toString(),
				uploadPropositionFilter.getDomainUuid(), uploadPropositionFilter.getName(),
				uploadPropositionFilter.getMatchType(), uploadPropositionFilter.getUploadPropositionAction(),
				uploadPropositionFilter.isEnabled(), uploadPropositionFilter.getOrder(),
				uploadPropositionFilter.getUploadPropositionRules(), new Date(), new Date());
		return businessService.create(createdFilter);
	}

	@Override
	public UploadPropositionFilter update(Account authUser, AbstractDomain domain,
			UploadPropositionFilter uploadPropositionFilter) throws BusinessException {
		preChecks(authUser);
		validateEntryValues(uploadPropositionFilter);
		Validate.notEmpty(uploadPropositionFilter.getUuid(), "filter uuid is required");
		Validate.notNull(uploadPropositionFilter.getCreationDate(), "CreationDate is required");
		Validate.notNull(uploadPropositionFilter.getOrder());
		Validate.notNull(uploadPropositionFilter.getModificationDate(), "Modification is required");
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_CAN_NOT_CREATE,
					"You are not allowed to use this domain");
		}
		UploadPropositionFilter filterToUpdate = new UploadPropositionFilter(uploadPropositionFilter);
		find(authUser, domain, filterToUpdate.getUuid());
		return businessService.update(filterToUpdate);
	}

	@Override
	public UploadPropositionFilter delete(Account authUser, AbstractDomain domain,
			UploadPropositionFilter uploadPropositionFilter) throws BusinessException {
		preChecks(authUser);
		Validate.notNull(uploadPropositionFilter, "filter is required");
		Validate.notEmpty(uploadPropositionFilter.getUuid(), "filter uuid is required");
		UploadPropositionFilter uploadPropositionFilterToDelete = find(authUser, domain,
				uploadPropositionFilter.getUuid());
		businessService.delete(uploadPropositionFilterToDelete);
		return uploadPropositionFilterToDelete;
	}

	@Override
	public void delete(Account actor, UploadPropositionRule entity) throws BusinessException {
		preChecks(actor);
		businessService.delete(entity);
	}

	@Override
	public void delete(Account actor, UploadPropositionAction entity) throws BusinessException {
		preChecks(actor);
		businessService.delete(entity);
	}
	
	void preChecks(Account actor) {
		Validate.notNull(actor, "actor is required");
		Validate.notEmpty(actor.getLsUuid(), "actor uuid is required");
	}

	private void validateEntryValues(UploadPropositionFilter uploadPropositionFilter) {
		Validate.notNull(uploadPropositionFilter, "Filter is required");
		Validate.notEmpty(uploadPropositionFilter.getDomainUuid(), "DomainUuid is required");
		Validate.notEmpty(uploadPropositionFilter.getName(), "Name is required");
		Validate.notEmpty(uploadPropositionFilter.getUploadPropositionRules(), "A filter must have at least one rule");
		Validate.notNull(uploadPropositionFilter.getMatchType(), "MatchType is required");
		Validate.notNull(uploadPropositionFilter.getUploadPropositionAction(), "Action is required");
	}
}
