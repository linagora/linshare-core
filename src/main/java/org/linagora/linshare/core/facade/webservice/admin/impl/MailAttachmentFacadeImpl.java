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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailAttachmentFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailAttachmentService;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class MailAttachmentFacadeImpl extends AdminGenericFacadeImpl implements MailAttachmentFacade {

	protected final  MailAttachmentService attachmentService;

	protected final AbstractDomainService abstractDomainService;

	public MailAttachmentFacadeImpl(
			AccountService accountService,
			MailAttachmentService attachmentService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.attachmentService = attachmentService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public MailAttachmentDto create(File tempFile, String fileName, String description, String metaData, boolean enable,
			boolean override, String mailConfig, String alt, String cid, int language) {
		Account authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(tempFile, "Missing required file (check parameter named file)");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(enable, "Missing information to enable mail attachment (enabled)");
		Validate.notNull(override, "Missing information to override the mail attachment (override)");
		Validate.notNull(mailConfig, "Missing mail config");
		Validate.notNull(alt, "Missing mail attachment alternative");
		MailAttachment attachment = attachmentService.create(authUser, enable, fileName, override, mailConfig,
				description, alt, cid, language, tempFile, metaData);
		return new MailAttachmentDto(attachment);
	}

	@Override
	public MailAttachmentDto delete(String uuid, MailAttachmentDto attachment) {
		Account authUser = checkAuthentication(Role.ADMIN);
		MailAttachment mailAttachment = new MailAttachment();
		if (!Strings.isNullOrEmpty(uuid)) {
			mailAttachment = attachmentService.find(authUser, uuid);
		} else {
			Validate.notNull(attachment, "MailAttachment object must be set");
			Validate.notEmpty(attachment.getUuid(), "MailAttachment uuid must be set");
			mailAttachment = attachmentService.find(authUser, attachment.getUuid());
		}
		mailAttachment = attachmentService.delete(authUser, mailAttachment);
		return new MailAttachmentDto(mailAttachment);
	}

	@Override
	public MailAttachmentDto find(String uuid) {
		Validate.notEmpty(uuid, "Missing required mail attachment uuid");
		Account authUser = checkAuthentication(Role.ADMIN);
		MailAttachment mailAttachment = attachmentService.find(authUser, uuid);
		return new MailAttachmentDto(mailAttachment);
	}

	@Override
	public List<MailAttachmentDto> findAll(String domainUuid) {
		Account authUser = checkAuthentication(Role.ADMIN);
		if (Strings.isNullOrEmpty(domainUuid)) {
			domainUuid = authUser.getDomain().getUuid();
		}
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		List<MailAttachment> attachments = attachmentService.findAllByDomain(authUser, domain);
		return ImmutableList.copyOf(Lists.transform(attachments, MailAttachmentDto.toDto()));
	}

	@Override
	public MailAttachmentDto update(MailAttachmentDto attachment, String uuid) throws BusinessException {
		Account authUser = checkAuthentication(Role.ADMIN);
		MailAttachment attachmentToUpdate = new MailAttachment();
		if (!Strings.isNullOrEmpty(uuid)) {
			attachmentToUpdate = attachmentService.find(authUser, uuid);
		} else {
			Validate.notNull(attachment, "MailAttachment object must be set");
			Validate.notEmpty(attachment.getUuid(), "MailAttachment uuid must be set");
			attachmentToUpdate = attachmentService.find(authUser, attachment.getUuid());
		}
		MailAttachment mailAttach = attachment.toObject();
		attachmentService.update(authUser, attachmentToUpdate, mailAttach);
		return new MailAttachmentDto(attachmentToUpdate);
	}
}
