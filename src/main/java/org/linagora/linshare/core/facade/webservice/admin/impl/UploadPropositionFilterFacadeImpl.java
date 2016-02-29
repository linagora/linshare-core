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

package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadPropositionFilterFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadPropositionFilterDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadPropositionFilterService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadPropositionFilterFacadeImpl extends AdminGenericFacadeImpl
		implements UploadPropositionFilterFacade {

	private final UploadPropositionFilterService service;

	public UploadPropositionFilterFacadeImpl(AccountService accountService,
			UploadPropositionFilterService service) {
		super(accountService);
		this.service = service;
	}

	@Override
	public List<UploadPropositionFilterDto> findAll() throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		List<UploadPropositionFilter> all = service.findAll(actor);
		return Lists.transform(ImmutableList.copyOf(all), UploadPropositionFilterDto.toVo());
	}

	@Override
	public UploadPropositionFilterDto find(String uuid)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = service.find(actor, uuid);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public UploadPropositionFilterDto create(UploadPropositionFilterDto dto)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = UploadPropositionFilterDto.toEntity().apply(dto);
		filter = service.create(actor, filter);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public UploadPropositionFilterDto update(UploadPropositionFilterDto dto)
			throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = UploadPropositionFilterDto.toEntity().apply(dto);
		filter = service.update(actor, filter);
		return UploadPropositionFilterDto.toVo().apply(filter);
	}

	@Override
	public UploadPropositionFilterDto delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.SUPERADMIN);
		UploadPropositionFilter filter = service.delete(actor, uuid);
		return new UploadPropositionFilterDto(filter);
	}

}
