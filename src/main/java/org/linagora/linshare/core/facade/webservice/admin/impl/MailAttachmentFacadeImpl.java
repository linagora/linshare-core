/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2019 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2019. Contribute to
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

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailAttachmentFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailAttachmentService;
import org.linagora.linshare.core.service.MailConfigService;
import org.linagora.linshare.mongo.entities.light.GenericLightEntity;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class MailAttachmentFacadeImpl extends AdminGenericFacadeImpl implements MailAttachmentFacade {

	protected final  MailAttachmentService attachmentService;

	protected final MailConfigService configService;

	public MailAttachmentFacadeImpl(
			AccountService accountService,
			MailAttachmentService attachmentService,
			MailConfigService configService) {
		super(accountService);
		this.attachmentService = attachmentService;
		this.configService = configService;
	}

	@Override
	public MailAttachmentDto create(File tempFile, String fileName, String description, String metaData, boolean enable,
			boolean enableForAll, String mailConfig, String cid, Language language) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(tempFile, "Missing required file (check parameter named file)");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(enable, "Missing information to enable mail attachment (enabled)");
		Validate.notNull(enableForAll, "Missing information to apply the mail attachment for all languages or not");
		Validate.notNull(mailConfig, "Missing mail config");
		MailAttachment attachment = attachmentService.create(authUser, enable, fileName, enableForAll, mailConfig,
				description, cid, language, tempFile, metaData);
		return getMailAttachmentDto(authUser, attachment);
	}

	private MailAttachmentDto getMailAttachmentDto(Account authUser, MailAttachment attachment) {
		GenericLightEntity genericMailConf = new GenericLightEntity(attachment.getMailConfig().getUuid(), attachment.getMailConfig().getName());
		MailAttachmentDto attachmentDto = new MailAttachmentDto(attachment);
		attachmentDto.setMailConfig(genericMailConf);
		return attachmentDto;
	}

	@Override
	public MailAttachmentDto delete(String uuid, MailAttachmentDto attachment) {
		Account authUser = checkAuthentication(Role.ADMIN);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(attachment, "Missing required attachment");
			Validate.notEmpty(attachment.getUuid(), "Missing required attachment uuid");
			uuid = attachment.getUuid();
		}
		Validate.notNull(uuid, "uuid must be set");
		MailAttachment mailAttachment = attachmentService.delete(authUser, uuid);
		return getMailAttachmentDto(authUser, mailAttachment);
	}

	@Override
	public MailAttachmentDto find(String uuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "Missing required mail attachment uuid");
		MailAttachment mailAttachment = attachmentService.find(authUser, uuid);
		return getMailAttachmentDto(authUser, mailAttachment);
	}

	@Override
	public List<MailAttachmentDto> findAll(String configUuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(configUuid, "configUuid must be set");
		MailConfig config = configService.findConfigByUuid((User) authUser, configUuid);
		List<MailAttachment> attachments = attachmentService.findAllByMailConfig(authUser, config);
		List<MailAttachmentDto> attachmentDtos = Lists.newArrayList();
		for (MailAttachment attachment : attachments) {
			attachmentDtos.add(getMailAttachmentDto(authUser, attachment));
		}
		return attachmentDtos;
	}

	@Override
	public MailAttachmentDto update(MailAttachmentDto attachment, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(attachment, "MailAttachment object must be set");
		if (!Strings.isNullOrEmpty(uuid)) {
			attachment.setUuid(uuid);
		}
		MailAttachment mailAttach = attachment.toObject();
		mailAttach = attachmentService.update(authUser, mailAttach);
		return getMailAttachmentDto(authUser, mailAttach);
	}

	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAudits(String uuid, List<LogAction> actions) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(uuid, "Missing required mail attachment uuid");
		MailAttachment attachment = attachmentService.find(authUser, uuid);
		return attachmentService.findAllAudits(authUser, attachment, actions);
	}
}
