/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryUrlBusinessService;
import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntryUrl;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.UploadRequestEntryUrlService;
import org.linagora.linshare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UploadRequestEntryUrlServiceImpl implements
		UploadRequestEntryUrlService {

	final private static Logger logger = LoggerFactory
			.getLogger(UploadRequestUrlServiceImpl.class);

	private final UploadRequestEntryUrlBusinessService uploadRequestEntryUrlBusinessService;

	private final DocumentEntryBusinessService documentEntryBusinessService;
	
	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public UploadRequestEntryUrlServiceImpl(
			UploadRequestEntryUrlBusinessService uploadRequestEntryUrlBusinessService,
			DocumentEntryBusinessService documentEntryBusinessService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		super();
		this.uploadRequestEntryUrlBusinessService = uploadRequestEntryUrlBusinessService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}

	@Override
	public boolean exists(String uploadRequestEntryUrlUuid, String urlPath)
			throws BusinessException {
		UploadRequestEntryUrl uploadRequestEntryUrl = uploadRequestEntryUrlBusinessService
				.findByUuid(uploadRequestEntryUrlUuid);

		if (uploadRequestEntryUrl.getPath().endsWith(urlPath)) {
			return true;
		}
		logger.error("the source path is different than the upload request entry url path : "
				+ urlPath + " : " + uploadRequestEntryUrl.getPath());
		return false;
	}

	@Override
	public boolean isProtectedByPassword(String uuid)
			throws BusinessException {
		UploadRequestEntryUrl uploadRequestEntryUrl = uploadRequestEntryUrlBusinessService
				.findByUuid(uuid);
		return !StringUtils.isEmpty(uploadRequestEntryUrl.getPassword());
	}

	@Override
	public boolean isValid(String uuid, String password)
			throws BusinessException {
		UploadRequestEntryUrl uploadRequestEntryUrl = uploadRequestEntryUrlBusinessService
				.findByUuid(uuid);
		return isValid(uploadRequestEntryUrl, password);
	}

	private boolean isValid(UploadRequestEntryUrl uploadRequestEntryUrl,
			String password) {
		if (!uploadRequestEntryUrlBusinessService
				.isExpired(uploadRequestEntryUrl)) {
			if (password != null) {
				return uploadRequestEntryUrlBusinessService.isValidPassword(
						uploadRequestEntryUrl, password);
			} else {
				return true;
			}
		}
		return false;
	}

	private Date getExpiryDate(){
		SystemAccount actor = uploadRequestEntryUrlBusinessService
				.getUploadRequestEntryURLAccount();
		TimeUnitValueFunctionality expiryDateFunc = functionalityReadOnlyService
				.getUploadRequestEntryUrlExpiryTimeFunctionality(actor.getDomain());
		@SuppressWarnings("deprecation")
		Date expiryDate = DateUtils.add(new Date(), expiryDateFunc.toCalendarValue(), expiryDateFunc.getValue());
		return expiryDate;
	}

	@Override
	public InputStream retrieveFileStream(String uploadRequestEntryUrlUuid,
			String uploadRequestEntryUuid, String password)
			throws BusinessException {
		UploadRequestEntryUrl uploadRequestEntryUrl = uploadRequestEntryUrlBusinessService
				.findByUuid(uploadRequestEntryUrlUuid);
		if (isValid(uploadRequestEntryUrl, password)) {
			if (uploadRequestEntryUrl.getUploadRequestEntry().getUuid()
					.equals(uploadRequestEntryUuid)) {
				return documentEntryBusinessService
						.getDocumentStream(uploadRequestEntryUrl
								.getUploadRequestEntry().getDocumentEntry());
			}
			String msg = "uploadRequestEntryUuid not found : "
					+ uploadRequestEntryUuid;
			logger.debug(msg);
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_URL_NOT_FOUND, msg);
		}
		String msg = "uploadRequestEntryUrlUuid not valid : "
				+ uploadRequestEntryUrlUuid;
		logger.debug(msg);
		throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_URL_EXPIRED, msg);
	}

	@Override
	public UploadRequestEntryUrl find(String uuid, String password)
			throws BusinessException {
		Validate.notEmpty(uuid);
		UploadRequestEntryUrl requestEntryUrl = uploadRequestEntryUrlBusinessService
				.findByUuid(uuid);
		if (requestEntryUrl != null) {
			accessBusinessCheck(requestEntryUrl, password);
			return requestEntryUrl;
		}
		throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_URL_NOT_FOUND,
				"this upload request entry url does not exit: "
						+ uuid);
	}

	@Override
	public UploadRequestEntryUrl create(UploadRequestEntry requestEntry,
			boolean secured) throws BusinessException {
		return uploadRequestEntryUrlBusinessService.create(requestEntry,
				secured, getExpiryDate());
	}

	private boolean isValidPassword(UploadRequestEntryUrl data, String password) {
		if (data.isProtectedByPassword()) {
			if (password == null)
				return false;
			String hashedPassword = HashUtils.hashSha1withBase64(password
					.getBytes());
			return hashedPassword.equals(data.getPassword());
		}
		return true;
	}

	private void accessBusinessCheck(UploadRequestEntryUrl requestEntryUrl,
			String password) throws BusinessException {
		if (!isValidPassword(requestEntryUrl, password)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_URL_FORBIDDEN,
					"You do not have the right to get this upload request url : "
							+ requestEntryUrl.getUuid());
		}

		Calendar now = GregorianCalendar.getInstance();
		Calendar compare = GregorianCalendar.getInstance();
		compare.setTime(requestEntryUrl.getExpiryDate());
		if (now.after(compare)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_ENTRY_URL_EXPIRED,
					"The current upload request url is outdated : "
							+ requestEntryUrl.getUuid());
		}
	}

	@Override
	public void deleteUploadRequestEntryUrl(
			Account actor, UploadRequestEntryUrl uploadRequestEntryUrl)
			throws BusinessException {
		Validate.notNull(actor);
		Validate.notNull(uploadRequestEntryUrl);
		if( !actor.hasSuperAdminRole() )
			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
					"the actor has not the right to delete this upload request entry url"
							+ uploadRequestEntryUrl.getUuid());
		uploadRequestEntryUrlBusinessService.delete(uploadRequestEntryUrl);
	}

	@Override
	public UploadRequestEntry getUploadRequestEntry(
			String uploadRequestUrlUuid, String password) {
		UploadRequestEntry res = new UploadRequestEntry();
		UploadRequestEntryUrl uploadRequestEntryUrl = uploadRequestEntryUrlBusinessService
				.findByUuid(uploadRequestUrlUuid);
		if (isValid(uploadRequestEntryUrl, password)) {
			res = uploadRequestEntryUrl.getUploadRequestEntry();
		}
		return res;
	}

	@Override
	public List<UploadRequestEntryUrl> findAllExpiredUploadRequestEntryUrl(
			Account actor) throws BusinessException {
		Validate.notNull(actor);
		if (actor.getRole() == Role.SUPERADMIN)
			return uploadRequestEntryUrlBusinessService
					.findAllExpiredUploadRequestEntryUrl();
		throw new BusinessException(BusinessErrorCode.FORBIDDEN,
				"Actor role must be SUPERADMIN");
	}
}
