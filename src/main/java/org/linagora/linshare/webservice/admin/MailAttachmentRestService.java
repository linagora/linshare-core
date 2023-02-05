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
package org.linagora.linshare.webservice.admin;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailAttachmentDto;
import org.linagora.linshare.mongo.entities.logs.MailAttachmentAuditLogEntry;

public interface MailAttachmentRestService {

	MailAttachmentDto create(InputStream file, String description, String givenFileName, String metaData, Long fileSize,
			boolean enable, boolean enableForAll, String configUuid, String cid, Language language, MultipartBody body)
			throws BusinessException;

	MailAttachmentDto delete(String uuid, MailAttachmentDto attachment) throws BusinessException;

	MailAttachmentDto find(String uuid) throws BusinessException;

	List<MailAttachmentDto> findAll(String configUuid) throws BusinessException;

	MailAttachmentDto update(String uuid, MailAttachmentDto attachment) throws BusinessException;

	Set<MailAttachmentAuditLogEntry> findAllAudits(String uuid, List<LogAction> actions);

	Set<MailAttachmentAuditLogEntry> findAllAuditsByDomain(String domainUuid, List<LogAction> actions);
}
