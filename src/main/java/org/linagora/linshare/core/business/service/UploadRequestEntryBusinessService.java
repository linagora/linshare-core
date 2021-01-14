/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
package org.linagora.linshare.core.business.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
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

	UploadRequestEntry findRelative(DocumentEntry entry);

	UploadRequestEntry createUploadRequestEntryDocument(Account owner, File myFile, Long size, String fileName,
			String comment, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate,
			boolean isFromCmis, String metadata, UploadRequestUrl uploadRequestUrl) throws BusinessException;

	String SHA256CheckSumFileStream(File file);

	String SHA256CheckSumFileStream(InputStream fis) throws IOException;

	byte[] getTimeStamp(String fileName, File tempFile, String timeStampingUrl) throws BusinessException;

	ByteSource  download(UploadRequestEntry entry);

	List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl);
}
