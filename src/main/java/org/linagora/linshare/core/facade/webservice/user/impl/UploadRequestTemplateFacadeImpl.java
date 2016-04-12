/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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

package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.entities.UploadRequestTemplate;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestTemplateFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.UploadRequestTemplateDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UploadRequestService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class UploadRequestTemplateFacadeImpl extends GenericFacadeImpl implements UploadRequestTemplateFacade {

	private UploadRequestService uploadRequestService;

	public UploadRequestTemplateFacadeImpl(AccountService accountService,
			final UploadRequestService uploadRequestService) {
		super(accountService);
		this.uploadRequestService = uploadRequestService;
	}

	@Override
	public List<UploadRequestTemplateDto> findAll(String ownerUuid) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Set<UploadRequestTemplate> tempSet = owner.getUploadRequestTemplates();
		return ImmutableList.copyOf(Lists.transform(Lists.newArrayList(tempSet), UploadRequestTemplateDto.toDto()));
	}

	@Override
	public UploadRequestTemplateDto find(String ownerUuid, String uuid) throws BusinessException {
		Validate.notEmpty(uuid, "Template uuid must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequestTemplate template = uploadRequestService.findTemplateByUuid(actor, owner, uuid);
		return new UploadRequestTemplateDto(template);
	}

	@Override
	public UploadRequestTemplateDto create(String ownerUuid, UploadRequestTemplateDto dto)
			throws BusinessException {
		Validate.notNull(dto, "Template must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequestTemplate template = dto.toObject();
		template = uploadRequestService.createTemplate(actor, owner, template);
		return new UploadRequestTemplateDto(template);
	}

	@Override
	public UploadRequestTemplateDto update(String ownerUuid, String uuid, UploadRequestTemplateDto dto)
			throws BusinessException {
		Validate.notNull(dto, "Template must be set.");
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		UploadRequestTemplate template = dto.toObject();
		template = uploadRequestService.updateTemplate(actor, owner, uuid, template);
		return new UploadRequestTemplateDto(template);
	}

	@Override
	public UploadRequestTemplateDto delete(String ownerUuid, String uuid) throws BusinessException {
		User actor = checkAuthentication();
		User owner = getOwner(actor, ownerUuid);
		Validate.notEmpty(uuid, "Template uuid must be set.");
		UploadRequestTemplate template = uploadRequestService.deleteTemplate(actor, owner, uuid);
		return new UploadRequestTemplateDto(template);
	}
}
