/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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
package org.linagora.linshare.core.facade.webservice.user;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.CopyDto;
import org.linagora.linshare.core.facade.webservice.common.dto.DocumentAttachement;
import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.utils.Version;

import com.google.common.io.ByteSource;

public interface DocumentFacade extends GenericFacade {

	List<DocumentDto> findAll(Version version) throws BusinessException;

	DocumentDto find(Version version, String uuid, boolean withShares) throws BusinessException;

	DocumentDto addDocumentXop(DocumentAttachement doca)
			throws BusinessException;

	DocumentDto create(File tempFile, String fileName,
			String description, String metadata) throws BusinessException;

	DocumentDto createWithSignature(File tempFile, String fileName,
			String description, InputStream signatureFile, String signatureFileName, InputStream x509certificate) throws BusinessException;

	ByteSource getByteSource(String docEntryUuid)
			throws BusinessException;

	ByteSource getThumbnailByteSource(String docEntryUuid, ThumbnailType kind)
			throws BusinessException;

	DocumentDto delete(String uuid) throws BusinessException;

	Boolean isEnableMimeTypes() throws BusinessException;

	List<MimeTypeDto> getMimeTypes() throws BusinessException;

	DocumentDto update(String documentUuid, DocumentDto documentDto) throws BusinessException;

	DocumentDto updateFile(File file, String givenFileName,
			String documentUuid) throws BusinessException;

	Set<AuditLogEntryUser> findAll(String actorUuid, String uuid, List<LogAction> actions,
			List<AuditLogEntryType> types, String beginDate, String endDate);

	List<DocumentDto> copy(String actorUuid, CopyDto  copy, boolean deleteShare) throws BusinessException;

}
