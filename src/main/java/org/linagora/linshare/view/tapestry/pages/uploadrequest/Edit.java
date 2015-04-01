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
package org.linagora.linshare.view.tapestry.pages.uploadrequest;

import java.text.DateFormat;
import java.util.Date;

import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.FieldValidatorSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.vo.UploadRequestVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UploadRequestFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.BSBeanEditForm;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;

public class Edit {

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	@Persist
	private UploadRequestVo selected;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Inject
	private PersistentLocale persistentLocale;
	
	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private UploadRequestFacade uploadRequestFacade;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private FieldValidatorSource source;

	@InjectComponent
	private TextField maxFileCount;

	@InjectComponent
	private TextField maxFileSize;

	@InjectComponent
	private TextField maxDepositSize;

	@InjectComponent
	private BSBeanEditForm bsBeanEditForm;
	
	private Long _d;

	private Long _s;

	private Integer _c;
	
	private Date _expiration;

	private FileSizeUnit maxDepositSizeUnit;

	private Long maxDepositSizeValue;

	private FileSizeUnit maxFileSizeUnit;

	private Long maxFileSizeValue;

	public Object onActivate(String uuid) {
		logger.debug("Upload Request uuid: " + uuid);
		try {
			this.selected = uploadRequestFacade.findRequestByUuid(userVo, uuid);
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.UPLOAD_REQUEST_NOT_FOUND,
					MessageSeverity.ERROR));
			return Index.class;
		}
		return null;
	}

	public Object onActivate() {
		if (!functionalityFacade.isEnableUploadRequest(userVo
				.getDomainIdentifier())) {
			return Index.class;
		}
		if (selected == null) {
			logger.info("No upload request selected, abort");
			return Index.class;
		}
		if (!selected.getOwner().businessEquals(userVo)
				|| selected.getStatus().equals(
						UploadRequestStatus.STATUS_ARCHIVED)) {
			logger.info("Unauthorized");
			businessMessagesManagementService.notify(new BusinessUserMessage(
					BusinessUserMessageType.UPLOAD_REQUEST_NOT_FOUND,
					MessageSeverity.ERROR));
			return Index.class;
		}
		if (maxDepositSizeUnit == null) {
			if (selected.getMaxDepositSize() != null) {
				maxDepositSizeUnit = FileSizeUnit.getMaxExactPlainSizeUnit(selected.getMaxDepositSize());
			}
		}
		if (maxFileSizeUnit == null) {
			maxFileSizeUnit = FileSizeUnit.getMaxExactPlainSizeUnit(selected.getMaxFileSize());
		}
		UploadRequestVo def;
		try {
			def = uploadRequestFacade.getDefaultValue(userVo, beanModelSource
					.createEditModel(UploadRequestVo.class, messages));
			_d = def.getMaxDepositSize();
			_s = def.getMaxFileSize();
			_c = def.getMaxFileCount();
			_expiration = def.getExpiryDate();
		} catch (BusinessException e) {
			logger.error("Cannot get default upload request value for user "
					+ userVo.getLsUuid());
			businessMessagesManagementService.notify(e);
			return Index.class;
		}
		return null;
	}

	public Object onPassivate() {
		return selected.getUuid();
	}

	@Log
	public void onValidateFromExpiryDate(Date toValidate) throws BusinessException {
		if (toValidate.after(_expiration)) {
			String localizedExpirationDate = DateFormat.getDateInstance(DateFormat.SHORT, persistentLocale.get()).format(_expiration);
			bsBeanEditForm.recordError(messages.format("pages.uploadrequest.validation.expiryDateTooLate", localizedExpirationDate));
		}
		Date now = new Date();
		if (toValidate.before(now)) {
			bsBeanEditForm.recordError(messages.get("pages.uploadrequest.validation.expiryDateBeforeNow"));
		}
	}
	
	@Log
	public void onValidateFromMaxDepositSizeUnit(FileSizeUnit unit) throws BusinessException {
		long plainSize = unit.getPlainSize(maxDepositSizeValue);
		if (plainSize > _d) {
			bsBeanEditForm.recordError(messages.format("pages.uploadrequest.validation.sizeMax", formatSizeValue(_d), maxDepositSize.getLabel()));
		}
	}

	@Log
	public void onValidateFromMaxFileSizeUnit(FileSizeUnit unit) throws BusinessException {
		long plainSize = unit.getPlainSize(maxFileSizeValue);
		if (plainSize > _s) {
			bsBeanEditForm.recordError(messages.format("pages.uploadrequest.validation.sizeMax", formatSizeValue(_s), maxFileSize.getLabel()));
		}
	}

	@Log
	public Object onSuccess() throws BusinessException {
		uploadRequestFacade.updateRequest(userVo, selected);
		return Detail.class;
	}

	@Log
	public Object onCanceled() throws BusinessException {
		return Detail.class;
	}

	public void setMySelected(UploadRequestVo selected) {
		this.selected = selected;
	}

	public Long getMaxDepositSize() {
		return maxDepositSizeUnit.fromPlainSize(selected.getMaxDepositSize().longValue());
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSizeValue = maxDepositSize;
	}

	public FileSizeUnit getMaxDepositSizeUnit() {
		return maxDepositSizeUnit;
	}

	public void setMaxDepositSizeUnit(FileSizeUnit fileSizeUnit) {
		maxDepositSizeUnit = fileSizeUnit;
		selected.setMaxDepositSize(maxDepositSizeUnit.getPlainSize(maxDepositSizeValue));
	}

	public Long getMaxFileSize() {
		return maxFileSizeUnit.fromPlainSize(selected.getMaxFileSize().longValue());
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSizeValue = maxFileSize;
	}

	public FileSizeUnit getMaxFileSizeUnit() {
		return maxFileSizeUnit;
	}

	public void setMaxFileSizeUnit(FileSizeUnit fileSizeUnit) {
		maxFileSizeUnit = fileSizeUnit;
		selected.setMaxFileSize(maxFileSizeUnit.getPlainSize(maxFileSizeValue));
	}

	public String getMaxDepositSizeInformation() {
		return messages.format("pages.uploadrequest.validation.max-label", formatSizeValue(_d));
	}

	public String getMaxFileSizeInformation() {
		return messages.format("pages.uploadrequest.validation.max-label", formatSizeValue(_s));
	}

	private String formatSizeValue(long value) {
		FileSizeUnit unit = FileSizeUnit.getMaxExactPlainSizeUnit(_s);
		return String.format("%d %s", unit.fromPlainSize(value), messages.get("FileSizeUnit." + unit.name() + ".short"));
	}

	/*
	 * Model
	 */

	public BeanModel<UploadRequestVo> getModel() throws BusinessException {
		return uploadRequestFacade.getEditModel(userVo, beanModelSource
				.createEditModel(UploadRequestVo.class, messages));
	}

	/*
	 * Dynamic validation
	 */

	public FieldValidator<?> getMaxFileCountValidator() {
		return source.createValidators(maxFileCount, "required, max=" + _c);
	}

	/*
	 * Exception Handling
	 */

	Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}
}
