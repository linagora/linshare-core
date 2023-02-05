/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.service;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MailAttachment;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;

public interface MailAttachmentService {

	MailAttachment create(Account authUser, boolean enable, String fileName, boolean enableForAll, String mailConfig,
			String description, String cid, Language language, File tempFile, String metaData);

	MailAttachment find(Account authUser, String uuid);

	MailAttachment delete(Account authUser, String uuid);

	List<MailAttachment> findAllByMailConfig(Account authUser, MailConfig config);

	MailAttachment update(Account authUser, MailAttachment mailAttach);

	Set<MailAttachmentAuditLogEntry> findAllAudits(Account authUser, MailAttachment attachment, List<LogAction> actions);

	Set<MailAttachmentAuditLogEntry> findAllAuditsByDomain(Account authUser, String domain,
			List<LogAction> actions);

	Set<MailAttachmentAuditLogEntry> findAllAuditsByRoot(Account authUser, List<LogAction> actions);
}
