package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryUrlBusinessService;
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
		Date expiryDate = DateUtils.add(new Date(), expiryDateFunc.toCalendarUnitValue(), expiryDateFunc.getValue());
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
			UploadRequestEntryUrl uploadRequestEntryUrl)
			throws BusinessException {
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
}
