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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionFilterBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionFilterServiceImpl implements UploadPropositionFilterService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFilterServiceImpl.class);

	private final UploadPropositionFilterBusinessService businessService;

	private final DomainBusinessService domainBusinessService;

	public UploadPropositionFilterServiceImpl(UploadPropositionFilterBusinessService businessService, DomainBusinessService domainBusinessService) {
		super();
		this.businessService = businessService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public UploadPropositionFilter find(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "filter uuid is required");
		UploadPropositionFilter filter = businessService.find(uuid);
		if (filter ==null) {
			logger.error(actor.getAccountRepresentation() + " is looking for missing filter uuid : " + uuid);
			throw new BusinessException(BusinessErrorCode.UPLOAD_PROPOSITION_FILTER_NOT_FOUND, "filter with uuid : " + uuid + " not found.");
		}
		return filter;
	}

	@Override
	public List<UploadPropositionFilter> findAll(Account actor) throws BusinessException {
		preChecks(actor);
		if (actor.hasSuperAdminRole() || actor.hasUploadPropositionRole()) {
			return businessService.findAll();
		} else {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get these filters.");
		}
	}

	@Override
	public List<UploadPropositionFilter> findAllEnabledFilters(Account actor) throws BusinessException {
		preChecks(actor);
		if (actor.hasSuperAdminRole() || actor.hasUploadPropositionRole()) {
			return businessService.findAllEnabledFilters();
		} else {
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, "You are not authorized to get these filters.");
		}
	}

	@Override
	public UploadPropositionFilter create(Account actor, UploadPropositionFilter dto) throws BusinessException {
		preChecks(actor);
		Validate.notNull(dto, "filter is required");
		dto.setDomain(domainBusinessService.getUniqueRootDomain());
		return businessService.create(dto);
	}

	@Override
	public UploadPropositionFilter update(Account actor, UploadPropositionFilter dto) throws BusinessException {
		preChecks(actor);
		Validate.notNull(dto, "filter is required");
		Validate.notEmpty(dto.getUuid(), "filter uuid is required");
		// permission check
		find(actor, dto.getUuid());
		return businessService.update(dto);
	}

	@Override
	public UploadPropositionFilter delete(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "filter uuid is required");
		UploadPropositionFilter entity = find(actor, uuid);
		businessService.delete(entity);
		return entity;
	}

	void preChecks(Account actor) {
		Validate.notNull(actor, "actor is required");
		Validate.notEmpty(actor.getLsUuid(), "actor uuid is required");
	}
}
