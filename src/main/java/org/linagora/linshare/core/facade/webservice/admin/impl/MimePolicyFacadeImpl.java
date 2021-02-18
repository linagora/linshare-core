/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.MimePolicy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MimePolicyFacade;
import org.linagora.linshare.core.facade.webservice.common.dto.MimePolicyDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MimePolicyService;

import com.google.common.collect.Sets;

public class MimePolicyFacadeImpl extends AdminGenericFacadeImpl implements
		MimePolicyFacade {

	private final MimePolicyService mimePolicyService;

	public MimePolicyFacadeImpl(final AccountService accountService,
			final MimePolicyService mimePolicyService) {
		super(accountService);
		this.mimePolicyService = mimePolicyService;
	}

	@Override
	public MimePolicyDto create(MimePolicyDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto, "MimePolicy dto must be set.");
		Validate.notEmpty(dto.getName(), "policy name must be set.");
		Validate.notEmpty(dto.getDomainId(),
				"domain identifier name must be set.");

		MimePolicy create = mimePolicyService.create(authUser, dto.getDomainId(),
				new MimePolicy(dto));
		return new MimePolicyDto(create);
	}

	@Override
	public MimePolicyDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		MimePolicy mimePolicy = mimePolicyService.find(authUser, uuid, false);
		mimePolicyService.delete(authUser, mimePolicy);
		return new MimePolicyDto(mimePolicy);
	}

	@Override
	public MimePolicyDto find(String uuid, boolean full)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		return new MimePolicyDto(mimePolicyService.find(authUser, uuid, full),
				full);
	}

	@Override
	public Set<MimePolicyDto> findAll(String domainId, boolean onlyCurrentDomain)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		if (domainId == null)
			domainId = authUser.getDomainId();
		Set<MimePolicy> all = mimePolicyService.findAll(authUser, domainId,
				onlyCurrentDomain);
		return transform(all);
	}

	private Set<MimePolicyDto> transform(Set<MimePolicy> all) {
		Set<MimePolicyDto> res = Sets.newHashSet();
		for (MimePolicy mimePolicy : all) {
			res.add(new MimePolicyDto(mimePolicy));
		}
		return res;
	}

	@Override
	public MimePolicyDto update(MimePolicyDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto, "MimePolicy dto must be set.");
		Validate.notEmpty(dto.getName(), "policy name must be set.");
		Validate.notEmpty(dto.getUuid(), "policy uuid must be set.");
		MimePolicy mimePolicy = mimePolicyService.update(authUser, new MimePolicy(
				dto));
		return new MimePolicyDto(mimePolicy);
	}

	@Override
	public MimePolicyDto enableAllMimeTypes(String uuid)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		MimePolicy mimePolicy = mimePolicyService.enableAllMimeTypes(authUser,
				uuid);
		return new MimePolicyDto(mimePolicy, true);
	}

	@Override
	public MimePolicyDto disableAllMimeTypes(String uuid)
			throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "MimePolicy uuid must be set.");
		MimePolicy mimePolicy = mimePolicyService.disableAllMimeTypes(authUser,
				uuid);
		return new MimePolicyDto(mimePolicy, true);
	}

}
