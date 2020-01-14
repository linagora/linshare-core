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
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.business.service.MailAttachmentBusinessService;
import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AuditLogEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.MailAttachmentService;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.MailAttachmentMto;
import org.linagora.linshare.mongo.repository.AuditAdminMongoRepository;

public class MailAttachmentServiceImpl implements MailAttachmentService {

	protected final MailAttachmentBusinessService attachmentBusinessService;

	protected final MailConfigBusinessService configService;

	protected final DocumentEntryService documentEntryService;

	protected final DomainPermissionBusinessService domainPermissionService;

	protected final AuditAdminMongoRepository mongoRepository;

	protected final AuditLogEntryService auditLogEntryService;

	public MailAttachmentServiceImpl(
			MailAttachmentBusinessService attachmentBusinessService,
			MailConfigBusinessService configService,
			DocumentEntryService documentEntryService,
			DomainPermissionBusinessService domainPermissionService,
			AuditAdminMongoRepository mongoRepository,
			AuditLogEntryService auditLogEntryService) {
		super();
		this.attachmentBusinessService = attachmentBusinessService;
		this.configService = configService;
		this.documentEntryService = documentEntryService;
		this.domainPermissionService = domainPermissionService;
		this.mongoRepository = mongoRepository;
		this.auditLogEntryService = auditLogEntryService;
	}

	@Override
	public MailAttachment create(Account authUser, boolean enable, String fileName, boolean enableForAll, String mailConfig,
			String description, String cid, Language language, File tempFile, String metaData) {
		checkAdminFor(authUser, authUser.getDomain());
		Validate.notNull(tempFile, "Missing required file (check parameter named file)");
		Validate.notEmpty(fileName, "Missing required file name");
		Validate.notNull(enable, "Missing information to enable mail attachment (enabled)");
		Validate.notNull(enableForAll, "Missing information to apply the mail attachment for all languages or not");
		Validate.notNull(mailConfig, "Missing mail config");
		MailConfig config = configService.findByUuid(mailConfig);
		MailAttachment attachment = attachmentBusinessService.create(authUser, enable, fileName, enableForAll, config,
				description, cid, language, tempFile, metaData);
		saveLog(authUser, LogAction.CREATE, attachment);
		return attachment;
	}

	@Override
	public MailAttachment find(Account authUser, String uuid) {
		checkAdminFor(authUser, authUser.getDomain());
		Validate.notNull(authUser, "AuthUser must be set.");
		Validate.notEmpty(uuid, "Mail attachment's Uuid must be set");
		MailAttachment found = attachmentBusinessService.findByUuid(uuid);
		if (found == null) {
			String message = "The requested mail attachment has not been found.";
			throw new BusinessException(BusinessErrorCode.MAIL_ATTACHMENT_NOT_FOUND, message);
		}
		saveLog(authUser, LogAction.GET, found);
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
	public List<MailAttachment> findAllByMailConfig(Account authUser, MailConfig config) {
		checkAdminFor(authUser, authUser.getDomain());
		Validate.notNull(config, "MailConfig must be set");
		return attachmentBusinessService.findAllByMailConfig(config);
	}

	@Override
	public MailAttachment delete(Account authUser, String uuid) {
		checkAdminFor(authUser, authUser.getDomain());
		Validate.notNull(uuid, "Mail attachment uuid must be set");
		MailAttachment mailAttachment = find(authUser, uuid);
		attachmentBusinessService.delete(mailAttachment);
		saveLog(authUser, LogAction.DELETE, mailAttachment);
		return mailAttachment;
	}

	@Override
	public MailAttachment update(Account authUser, MailAttachment mailAttach) {
		checkAdminFor(authUser, authUser.getDomain());
		Validate.notNull(mailAttach, "Mail attachment must be set");
		Validate.notEmpty(mailAttach.getName(), "Name must be set");
		MailAttachment attachmentToUpdate = find(authUser, mailAttach.getUuid());
		MailAttachmentAuditLogEntry log = new MailAttachmentAuditLogEntry(authUser, LogAction.UPDATE,
				AuditLogEntryType.MAIL_ATTACHMENT, attachmentToUpdate);
		attachmentToUpdate.setEnable(mailAttach.getEnable());
		attachmentToUpdate.setEnableForAll(mailAttach.getEnableForAll());
		attachmentToUpdate.setLanguage(mailAttach.getLanguage());
		attachmentToUpdate.setDescription(mailAttach.getDescription());
		attachmentToUpdate.setName(mailAttach.getName());
		attachmentToUpdate.setCid(mailAttach.getCid());
		attachmentToUpdate = attachmentBusinessService.update(attachmentToUpdate);
		log.setResourceUpdated(new MailAttachmentMto(attachmentToUpdate));
		mongoRepository.insert(log);
		return attachmentToUpdate;
	}

	protected MailAttachmentAuditLogEntry saveLog(Account authUser, LogAction action, MailAttachment resource) {
		MailAttachmentAuditLogEntry log = new MailAttachmentAuditLogEntry(authUser, action,
				AuditLogEntryType.MAIL_ATTACHMENT, resource);
		mongoRepository.insert(log);
		return log;
	}

	@Override
	public Set<MailAttachmentAuditLogEntry> findAllAudits(Account authUser, MailAttachment attachment,
			List<LogAction> actions) {
		checkAdminFor(authUser, authUser.getDomain());
		return auditLogEntryService.findAllAudits(authUser, attachment.getUuid(), actions);
	}
}
