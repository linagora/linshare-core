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
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.utils.Version;

import com.google.common.io.ByteSource;

public interface DocumentFacade extends GenericFacade {

	List<DocumentDto> findAll(Version version) throws BusinessException;

	/**
	 * Retrieve a document by UUID and optionally enrich it with its associated shares.
	 *
	 * <p>
	 * This method returns a {@link DocumentDto} built from the document identified by the given UUID,
	 * and if {@code withShares} is true, includes all anonymous and regular shares related to this document.
	 * </p>
	 *
	 * <p>
	 * When {@code withShares} is enabled:
	 * <ul>
	 *   <li>Anonymous shares are retrieved using {@code findAllMyAnonymousShareEntries}.</li>
	 *   <li>Standard shares are retrieved using {@code findAllMyShareEntries} and processed according to
	 *       the contact list visibility of the current authenticated user.</li>
	 * </ul>
	 * </p>
	 *
	 * <p><b>Contact List Visibility:</b></p>
	 * <ul>
	 *   <li>If a share is associated with a contact list and the user is a guest who does not have permission
	 *       to view the members of that list (i.e., {@code canViewContactListMembers == false}),
	 *       then only the contact list name will be shown in the share (using a special {@code ShareDto}).</li>
	 *   <li>For each individual member of such a contact list, the list name will appear **repeated** in the display,
	 *       once per member.</li>
	 *   <li>If the same member exists in **multiple contact lists**, and the user does not have visibility
	 *       over any of them, the displayed list name might not reflect the actual list chosen by the sender.
	 *       In this case, the list name shown is **not guaranteed** to match the user’s intent. It will be the
	 *       first list found matching the member and the guest's visibility restrictions.</li>
	 *   <li>If the contact list was deleted, its name will be recovered from the audit logs when available.
	 *       The result will indicate the list was deleted and the visibility will be set to false.</li>
	 * </ul>
	 *
	 * @param version     the API version used to build the {@link DocumentDto} and {@link ShareDto}
	 * @param uuid        the UUID of the document to retrieve (must not be null or empty)
	 * @param withShares  whether to include share information in the response
	 * @return the document DTO enriched with share data if requested
	 * @throws BusinessException if the document cannot be found or another service-level error occurs
	 */
	DocumentDto find(Version version, String uuid, boolean withShares) throws BusinessException;

	DocumentDto addDocumentXop(DocumentAttachement doca)
			throws BusinessException;

	DocumentDto create(File tempFile, String fileName,
			String description, String metadata) throws BusinessException;

	DocumentDto createWithSignature(File tempFile, String fileName,
			String description, InputStream signatureFile, String signatureFileName, InputStream x509certificate) throws BusinessException;

	ByteSource getByteSource(String docEntryUuid)
			throws BusinessException;

	ByteSource getByteSourceRange(String docEntryUuid, long rangeOffset, long rangeLength)
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
