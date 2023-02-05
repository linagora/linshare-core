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
package org.linagora.linshare.core.business.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessException;

import com.google.common.io.ByteSource;

public interface UploadRequestEntryBusinessService {

	UploadRequestEntry findByUuid(String uuid);

	UploadRequestEntry create(UploadRequestEntry entry)
			throws BusinessException;

	UploadRequestEntry update(UploadRequestEntry entry)
			throws BusinessException;

	void delete(UploadRequestEntry entry) throws BusinessException;

	UploadRequestEntry createUploadRequestEntryDocument(Account owner, File myFile, Long size, String fileName,
			String comment, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate,
			boolean isFromCmis, String metadata, UploadRequestUrl uploadRequestUrl) throws BusinessException;

	String SHA256CheckSumFileStream(File file);

	String SHA256CheckSumFileStream(InputStream fis) throws IOException;

	byte[] getTimeStamp(String fileName, File tempFile, String timeStampingUrl) throws BusinessException;

	ByteSource  download(UploadRequestEntry entry);

	List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl);

	List<UploadRequestEntry> findAllEntries(UploadRequest uploadRequest);

	Boolean exist(UploadRequest uploadRequest, String EntryUuid);
}
