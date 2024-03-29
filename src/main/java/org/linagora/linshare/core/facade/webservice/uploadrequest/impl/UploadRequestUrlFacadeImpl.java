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
package org.linagora.linshare.core.facade.webservice.uploadrequest.impl;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.PasswordService;
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
import org.linagora.linshare.webservice.uploadrequestv5.dto.OneTimePasswordDto;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

public class UploadRequestUrlFacadeImpl extends GenericFacadeImpl implements UploadRequestUrlFacade {

	private final UploadRequestService uploadRequestService;

	private final UploadRequestUrlService uploadRequestUrlService;

	private final MimePolicyService mimePolicyService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final UploadRequestEntryService uploadRequestEntryService;

	private final PasswordService passwordService;

	private Cache<String, OneTimePasswordDto> oneTimePasswordCache;

	public UploadRequestUrlFacadeImpl(final AccountService accountService,
			final UploadRequestService uploadRequestService,
			final UploadRequestUrlService uploadRequestUrlService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final MimePolicyService mimePolicyService,
			final PasswordService passwordService,
			UploadRequestEntryService uploadRequestEntryService) {
		super(accountService);
		this.uploadRequestService = uploadRequestService;
		this.uploadRequestUrlService = uploadRequestUrlService;
		this.mimePolicyService = mimePolicyService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.uploadRequestEntryService = uploadRequestEntryService;
		this.passwordService = passwordService;
		oneTimePasswordCache = CacheBuilder.newBuilder()
				.expireAfterAccess(60, TimeUnit.SECONDS)
				.build();
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
	public List<UploadRequestEntryDto> findAllExtEntries(Integer version, String uuid, String password) {
		Validate.notEmpty(uuid, "Upload request url uuid must be set.");
		UploadRequestUrl requestUrl = uploadRequestUrlService.find(uuid, password);
		List<UploadRequestEntry> uploadRequestEntries = uploadRequestService.findAllExtEntries(requestUrl);
		return ImmutableList.copyOf(Lists.transform(uploadRequestEntries, UploadRequestEntryDto.toDto(version)));
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
	public Response thumbnail(String uploadRequestUrlUuid, String password, String uploadRequestEntryUuid, boolean base64, ThumbnailType thumbnailType) {
		Validate.notEmpty(uploadRequestUrlUuid, "Missing required uploadRequestUrlUuid");
		Validate.notEmpty(uploadRequestEntryUuid, "Missing required uploadRequestEntryUuid");
		SystemAccount authUser = uploadRequestUrlService.getUploadRequestSystemAccount();
		UploadRequestEntry uploadRequestEntry = checkUploadRequestUrlAndGetEntry(authUser, uploadRequestUrlUuid, password,
				uploadRequestEntryUuid);
		FileAndMetaData data = uploadRequestEntryService.thumbnail(authUser, authUser, uploadRequestEntry.getUuid(),
				thumbnailType);
		ResponseBuilder builder = DocumentStreamReponseBuilder.getThumbnailResponseBuilder(data, base64, thumbnailType);
		return builder.build();
	}

	@Override
	public Response download(String uploadRequestUrlUuid, Optional<String> otpPassword, String uploadRequestEntryUuid) {
		Validate.notEmpty(uploadRequestUrlUuid, "Missing required uploadRequestUrlUuid");
		Validate.notEmpty(uploadRequestEntryUuid, "Missing required uploadRequestEntryUuid");
		SystemAccount authUser = uploadRequestUrlService.getUploadRequestSystemAccount();
		Optional<String> password = Optional.absent();
		if (otpPassword.isPresent()) {
			Optional<OneTimePasswordDto> cachedOTP = Optional
					.fromNullable(oneTimePasswordCache.getIfPresent(uploadRequestEntryUuid));
			if (!cachedOTP.isPresent()) {
				throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN,
						"Invalid OTP password for uploadRequestEntry with uuid: " + uploadRequestEntryUuid);
			}
			if (!cachedOTP.get().getOtpPassword().equals(otpPassword.get())) {
				throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN,
						"Invalid OTP password for uploadRequestEntry with uuid: " + uploadRequestEntryUuid);
			}
			password = Optional.of(cachedOTP.get().getPassword());
			oneTimePasswordCache.invalidate(uploadRequestEntryUuid);
		}
		UploadRequestEntry uploadRequestEntry = checkUploadRequestUrlAndGetEntry(authUser, uploadRequestUrlUuid, password.orNull(),
				uploadRequestEntryUuid);
		ByteSource documentStream = uploadRequestEntryService.download(authUser, authUser, uploadRequestEntry.getUuid());
		FileAndMetaData data = new FileAndMetaData(documentStream, uploadRequestEntry.getSize(),
				uploadRequestEntry.getName(), uploadRequestEntry.getType());
		ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(data);
		return response.build();
	}

	@Override
	public Response download(String uploadRequestUrlUuid, String password, String uploadRequestEntryUuid) {
		Validate.notEmpty(uploadRequestUrlUuid, "Missing required uploadRequestUrlUuid");
		Validate.notEmpty(uploadRequestEntryUuid, "Missing required uploadRequestEntryUuid");
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

	@Override
	public OneTimePasswordDto create(String password, OneTimePasswordDto otp)
			throws BusinessException {
		Validate.notNull(otp, "Missing required otp");
		Validate.notEmpty(otp.getRequestUrlUuid(), "Missing required uploadRequestUrlUuid");
		Validate.notEmpty(otp.getEntryUuid(), "Missing required uploadRequestEntryUuid");
		SystemAccount authUser = uploadRequestUrlService.getUploadRequestSystemAccount();
		UploadRequestEntry uploadRequestEntry = checkUploadRequestUrlAndGetEntry(authUser, otp.getRequestUrlUuid(), password,
				otp.getEntryUuid());
		OneTimePasswordDto res = new OneTimePasswordDto(
				uploadRequestEntry.getUploadRequestUrl().getUuid(),
				uploadRequestEntry.getUuid(), password, passwordService.generatePassword());
		oneTimePasswordCache.put(uploadRequestEntry.getUuid(), res);
		return res;
	}
}
