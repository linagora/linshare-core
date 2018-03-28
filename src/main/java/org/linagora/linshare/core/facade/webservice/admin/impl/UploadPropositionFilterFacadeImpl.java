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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilterOLD;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadPropositionFilterFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadPropositionFilterDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;

public class UploadPropositionFilterFacadeImpl extends AdminGenericFacadeImpl
		implements UploadPropositionFilterFacade {

	private final UploadPropositionFilterService service;

	private final AbstractDomainService abstractDomainService;

	public UploadPropositionFilterFacadeImpl(final AccountService accountService,
			final UploadPropositionFilterService service,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.service = service;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<UploadPropositionFilter> findAll(String domainUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(domainUuid, "domain uuid must be set");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		return service.findAll(authUser, domain);
	}

	@Override
	public UploadPropositionFilter find(String uuid, String domainUuid)
			throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "uuid must be set");
		Validate.notEmpty(domainUuid, "domain uuid must be set");
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		return service.find(authUser, domain, uuid);
	}

	@Override
	public UploadPropositionFilter create(UploadPropositionFilter uploadPropositionFilter) throws BusinessException {
		Validate.notNull(uploadPropositionFilter, "The filter cannot be null");
		Validate.notEmpty(uploadPropositionFilter.getDomainUuid(), "domain uuid must be set");
		Validate.notEmpty(uploadPropositionFilter.getName(), "Name must be set");
		Validate.notNull(uploadPropositionFilter.getMatchType(), "The match type must be set");
		Validate.notNull(uploadPropositionFilter.getUploadPropositionAction(), "The action type must be set");
		Validate.notEmpty(uploadPropositionFilter.getUploadPropositionRules(), "A filter must have at least one rule");
		User authUser = checkAuthentication(Role.SUPERADMIN);
		AbstractDomain domain = abstractDomainService.findById(uploadPropositionFilter.getDomainUuid());
		return service.create(authUser, uploadPropositionFilter, domain);
	}

	@Override
	public UploadPropositionFilterDto update(UploadPropositionFilterDto dto)
			throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilterOLD filter = UploadPropositionFilterDto.toEntity().apply(dto);
		filter = service.update(authUser, filter);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public UploadPropositionFilterDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilterOLD filter = service.delete(authUser, uuid);
		return new UploadPropositionFilterDto(filter);
	}

}
