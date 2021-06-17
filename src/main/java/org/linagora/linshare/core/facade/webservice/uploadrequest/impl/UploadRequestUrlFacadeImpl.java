/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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

package org.linagora.linshare.core.facade.webservice.uploadrequest.impl;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestEntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.EntryDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.user.impl.GenericFacadeImpl;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.MimePolicyService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.UploadRequestService;
import org.linagora.linshare.core.service.UploadRequestUrlService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.ChangeUploadRequestUrlPassword;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

public class UploadRequestUrlFacadeImpl extends GenericFacadeImpl implements UploadRequestUrlFacade {

	private final UploadRequestService uploadRequestService;

	private final UploadRequestUrlService uploadRequestUrlService;

	private final MimePolicyService mimePolicyService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UploadRequestEntryService uploadRequestEntryService;

	public UploadRequestUrlFacadeImpl(final AccountService accountService,
			final UploadRequestService uploadRequestService,
			final UploadRequestUrlService uploadRequestUrlService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MimePolicyService mimePolicyService,
			UploadRequestEntryService uploadRequestEntryService) {
		super(accountService);
		this.uploadRequestService = uploadRequestService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mimePolicyService = mimePolicyService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.uploadRequestEntryService = uploadRequestEntryService;
	}

	@Override
	public UploadRequestDto find(String uploadRequestUrlUuid, String password) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid, "Upload request url uuid must be set");
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid, password);
		UploadRequestDto dto = transform(requestUrl);
		return dto;
	}

	@Override
	public UploadRequestDto close(String uuid, String password)
			throws BusinessException {
		Validate.notEmpty(uuid);
		UploadRequestUrl url = uploadRequestUrlService.find(uuid, password);
		uploadRequestService.closeRequestByRecipient(url);
		return transform(uploadRequestUrlService.find(uuid, password));
	}

	@Override
	public void addUploadRequestEntry(String uploadRequestUrlUuid,
			String password, File file, String fileName) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid, "Upload request url uuid must be set");
		Validate.notNull(file, "file must be set");
		Validate.notEmpty(fileName, "fileName must be set");
		uploadRequestUrlService.createUploadRequestEntry(uploadRequestUrlUuid, file, fileName, password);
	}

	@Override
	public UploadRequestEntryDto deleteUploadRequestEntry(String uploadRequestUrlUuid, String password,
			String entryUuid, EntryDto entry) throws BusinessException {
		Validate.notEmpty(uploadRequestUrlUuid, "Upload request url uuid must be set");
		if (Strings.isNullOrEmpty(entryUuid)) {
			Validate.notNull(entry, "Entry must be set");
			Validate.notEmpty(entry.getUuid(), "Entry uuid must be set");
			entryUuid = entry.getUuid();
		}
		UploadRequestEntry uploadRequestEntry = uploadRequestUrlService.deleteUploadRequestEntry(uploadRequestUrlUuid,
				password, entryUuid);
		return new UploadRequestEntryDto(uploadRequestEntry);
	}

	@Override
	public List<UploadRequestEntryDto> findAllExtEntries(String uuid, String password) {
		Validate.notEmpty(uuid, "Upload request url uuid must be set.");
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uuid, password);
		List<UploadRequestEntry> uploadRequestEntries = uploadRequestService.findAllExtEntries(requestUrl);
		return ImmutableList.copyOf(Lists.transform(uploadRequestEntries, UploadRequestEntryDto.toDto()));
	}


	@Override
	public void changePassword(String uuid, ChangeUploadRequestUrlPassword reset) {
		Validate.notEmpty(uuid, "Upload request url uuid must be set");
		Validate.notNull(reset);
		Validate.notEmpty(reset.getNewPassword(), "Missing new password");
		Validate.notEmpty(reset.getOldPassword(), "Missing old password");
		SystemAccount authUser = uploadRequestUrlService.getUploadRequestSystemAccount();
		uploadRequestUrlService.changePassword(authUser, authUser, uuid, reset);
	}

	/*
	 * Helpers
	 */

	private UploadRequestDto transform(UploadRequestUrl requestUrl) throws BusinessException {
		if (requestUrl == null) {
			return null;
		}
		UploadRequestDto dto = UploadRequestDto.toDto(requestUrl);
		Account actor = requestUrl.getUploadRequest().getUploadRequestGroup().getOwner();
		Functionality functionality = functionalityReadOnlyService.getMimeTypeFunctionality(actor.getDomain());
		if (functionality.getActivationPolicy().getStatus()) {
			Set<MimeType> mimeTypes = mimePolicyService.findAllMyMimeTypes(actor);
			for (MimeType mimeType : mimeTypes) {
				String extension = mimeType.getExtensions();
				if (mimeType.getEnable()) {
					if (!extension.isEmpty()) {
						dto.addExtensions(extension.substring(1));
					}
					if (extension.equals(".jpg")) {
						dto.addExtensions("jpeg");
					}
				}
			}
		}
		dto.setUsedSpace(uploadRequestService.computeEntriesSize(requestUrl.getUploadRequest()));
		dto.setNbrUploadedFiles(uploadRequestService.countNbrUploadedFiles(requestUrl.getUploadRequest()));
		return dto;
	}

	@Override
	public Response thumbnail(String uploadRequestEntryUuid, boolean base64, ThumbnailType thumbnailType) {
		Validate.notEmpty(uploadRequestEntryUuid, "Missing required uploadRequestEntryUuid");
		SystemAccount authUser = uploadRequestUrlService.getUploadRequestSystemAccount();
		FileAndMetaData data = uploadRequestEntryService.thumbnail(authUser, authUser, uploadRequestEntryUuid,
				thumbnailType);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(data, base64, thumbnailType);
		return builder.build();
	}

	@Override
	public Response download(String uploadRequestUrlUuid, String password, String uploadRequestEntryUuid) {
		Validate.notEmpty(uploadRequestEntryUuid, "Missing required uploadRequestEntryUuid");
		Validate.notEmpty(uploadRequestUrlUuid, "Missing required uploadRequestUrlUuid");
		SystemAccount authUser = uploadRequestUrlService.getUploadRequestSystemAccount();
		UploadRequestEntry uploadRequestEntry = checkUploadRequestUrlAndGetEntry(authUser, uploadRequestUrlUuid, password,
				uploadRequestEntryUuid);
		ByteSource documentStream = uploadRequestEntryService.download(authUser, authUser, uploadRequestEntry.getUuid());
		FileAndMetaData data = new FileAndMetaData(documentStream, uploadRequestEntry.getSize(),
				uploadRequestEntry.getName(), uploadRequestEntry.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return response.build();
	}

	private UploadRequestEntry checkUploadRequestUrlAndGetEntry(Account authUser, String uploadRequestUrlUuid,
			String password, String uploadRequestEntryUuid) {
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uploadRequestUrlUuid, password);
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryService.find(authUser, authUser,
				uploadRequestEntryUuid);
		if (!uploadRequestEntryService.exist(authUser, authUser, uploadRequestEntry.getUuid(), requestUrl.getUploadRequest())) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN,
					"The choosen uploadRequestUrl with uuid: " + requestUrl.getUuid()
							+ " does not contain the uploadRequestEntry with uuid: " + uploadRequestEntry.getUuid());
		}
		return uploadRequestEntry;
	}
}
