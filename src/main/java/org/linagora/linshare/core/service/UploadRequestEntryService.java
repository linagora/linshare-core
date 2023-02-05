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

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.utils.FileAndMetaData;

import com.google.common.io.ByteSource;

public interface UploadRequestEntryService {

	UploadRequestEntry create(Account actor, Account owner, File tempFile, String fileName, String comment,
			boolean isFromCmis, String metadata, UploadRequestUrl uploadRequestUrl) throws BusinessException;

	boolean mimeTypeFilteringStatus(Account actor) throws BusinessException;

	UploadRequestEntry find (Account authUser, Account actor, String uuid);

	ByteSource download(Account actor, Account owner, String uuid) throws BusinessException;

	UploadRequestEntry delete(Account authUser, Account actor, String uuid);

	UploadRequestEntry deleteEntryByRecipients(UploadRequestUrl uploadRequestUrl, String entryUuid) throws BusinessException;

	List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl);

	FileAndMetaData downloadEntries(Account authUser, Account actor, UploadRequestGroup uploadRequestGroup,
			List<UploadRequestEntry> entries);

	DocumentEntry copy(Account actor, Account owner, CopyResource cr) throws BusinessException;

	List<UploadRequestEntry> findAllEntries(Account authUser, Account actor, UploadRequest uploadRequest);

	void delFromQuota(Account owner, Long size);

	FileAndMetaData thumbnail(Account authUser, Account actor, String uploadRequestEntryUuid,
			ThumbnailType thumbnailType);

	Boolean exist(Account authUser, Account actor, String entryUuid, UploadRequest uploadRequest);
}
