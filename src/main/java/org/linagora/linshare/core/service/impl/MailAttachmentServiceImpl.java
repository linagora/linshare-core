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
package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MailAttachmentBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.MailAttachmentService;

public class MailAttachmentServiceImpl implements MailAttachmentService {

	protected final MailAttachmentBusinessService attachmentBusinessService;

	protected final MailConfigBusinessService configService;

	protected final DocumentEntryService documentEntryService;

	private final DomainPermissionBusinessService domainPermissionService;

	public MailAttachmentServiceImpl(
			MailAttachmentBusinessService attachmentBusinessService,
			MailConfigBusinessService configService,
			DocumentEntryService documentEntryService,
			DomainPermissionBusinessService domainPermissionService) {
		super();
		this.attachmentBusinessService = attachmentBusinessService;
		this.configService = configService;
		this.documentEntryService = documentEntryService;
		this.domainPermissionService = domainPermissionService;
	}

	@Override
	public MailAttachment create(Account authUser, boolean enable, String fileName, boolean override, String mailConfig,
			String description, String alt, String cid, int language, File tempFile, String metaData) {
		MailConfig config = configService.findByUuid(mailConfig);
		MailAttachment attachment = attachmentBusinessService.create(authUser, enable, fileName, override, config,
				description, alt, cid, language, tempFile, metaData);
		return attachment;
	}

	@Override
	public MailAttachment find(Account authUser, String uuid) {
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notEmpty(uuid, "Mail attachment's Uuid must be set");
		checkAdminFor(authUser, authUser.getDomain());
		MailAttachment found = attachmentBusinessService.findByUuid(uuid);
		if (found == null) {
			String message = "The requested mail attachment has not been found.";
			throw new BusinessException(BusinessErrorCode.MAIL_ATTACHMENT_NOT_FOUND, message);
		}
		return found;
	}

	private void checkAdminFor(Account actor, AbstractDomain domain) throws BusinessException {
		if (!domainPermissionService.isAdminforThisDomain(actor, domain)) {
			String msg = "The current actor " + actor.getAccountRepresentation()
					+ " does not have the right to perform this operation.";
			throw new BusinessException(BusinessErrorCode.FORBIDDEN, msg);
		}
	}

	@Override
	public List<MailAttachment> findAllByDomain(Account authUser, AbstractDomain domain) {
		Validate.notNull(domain, "domain must be set");
		checkAdminFor(authUser, domain);
		return attachmentBusinessService.findAllByDomain(domain);
	}

	@Override
	public MailAttachment delete(Account authUser, MailAttachment mailAttachment) {
		Validate.notNull(mailAttachment, "Mail attachment must be set");
		checkAdminFor(authUser, authUser.getDomain());
		attachmentBusinessService.delete(mailAttachment);
		return mailAttachment;
	}

	@Override
	public MailAttachment update(Account authUser, MailAttachment attachmentToUpdate, MailAttachment mailAttach) {
		Validate.notNull(attachmentToUpdate, "Mail attachment must be set");
		Validate.notEmpty(mailAttach.getName(), "Name must be set");
		checkAdminFor(authUser, authUser.getDomain());
		attachmentToUpdate.setEnable(mailAttach.getEnable());
		attachmentToUpdate.setOverride(mailAttach.getOverride());
		attachmentToUpdate.setLanguage(mailAttach.getLanguage());
		attachmentToUpdate.setDescription(mailAttach.getDescription());
		attachmentToUpdate.setName(mailAttach.getName());
		attachmentToUpdate.setCid(mailAttach.getCid());
		return attachmentBusinessService.update(attachmentToUpdate);
	}
}
