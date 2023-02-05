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
package org.linagora.linshare.core.facade.webservice.uploadrequest;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;

import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.mongo.entities.ChangeUploadRequestUrlPassword;
import org.linagora.linshare.webservice.uploadrequestv5.dto.OneTimePasswordDto;

import com.google.common.base.Optional;

public interface UploadRequestUrlFacade {

	UploadRequestDto find(String uploadRequestUrlUuid, String password)
			throws BusinessException;

	UploadRequestDto close(String uuid, String password)
			throws BusinessException;

	void addUploadRequestEntry(String uploadRequestUrlUuid, String password, File tempFile, String fileName) throws BusinessException;

	UploadRequestEntryDto deleteUploadRequestEntry(String uploadRequestUrlUuid, String password, String entryUuid,
			EntryDto entry) throws BusinessException;

	List<UploadRequestEntryDto> findAllExtEntries(Integer version, String uuid, String password);

	void changePassword(String uuid, ChangeUploadRequestUrlPassword reset);

	Response thumbnail(String uploadRequestUrlUuid,  String password, String uploadRequestEntryUuid, boolean base64, ThumbnailType thumbnailType);

	Response download(String uploadRequestUrlUuid, String password, String uploadRequestEntryUuid);

	// since LinShare 6.0
	Response download(String uploadRequestUrlUuid, Optional<String> otpPassword, String uploadRequestEntryUuid);

	// since LinShare 6.0
	OneTimePasswordDto create(String password, OneTimePasswordDto otp) throws BusinessException;
}
