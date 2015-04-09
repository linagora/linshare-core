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
import java.util.List;

import org.apache.tapestry5.FieldValidator;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.ValueEncoder;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.TextField;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.FieldValidatorSource;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.util.EnumSelectModel;
import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.vo.UploadRequestTemplateVo;
import org.linagora.linshare.core.domain.vo.UploadRequestVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.UploadRequestFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.BSBeanEditForm;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.slf4j.Logger;


public class Create {

	/*
	 * Tapestry properties
	 */

	@SessionState
	@Property
	private ShareSessionObjects shareSessionObjects;

	@SessionState
	@Property
	private UserVo userVo;

	@Property
	private UploadRequestVo current;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private UploadRequestTemplateVo selected;

	@InjectComponent
	private Zone reload;

	@InjectComponent
	private TextField maxFileCount;

	@InjectComponent
	private TextField maxFileSize;

	@InjectComponent
	private TextField maxDepositSize;

	@InjectComponent
	private BSBeanEditForm bsBeanEditForm;

	/*
	 * Injected beans
	 */

	@Inject
	private FieldValidatorSource source;

	@Inject
	private Logger logger;

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private Messages messages;

	@Inject
	private PersistentLocale persistentLocale;

	@Property
	private SelectModel languageModel;

	@Inject
	private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private FunctionalityFacade functionalityFacade;

	@Inject
	private UploadRequestFacade uploadRequestFacade;

	private Long _d;

	private Long _s;

	private Integer _c;

	private Date _expiration;

	private FileSizeUnit maxDepositSizeUnit;

	private Long maxDepositSizeValue;

	private FileSizeUnit maxFileSizeUnit;

	private Long maxFileSizeValue;

	@Inject
	@Symbol("linshare.uploadrequest.templates.enable")
	@Property
	private boolean templatesEnable;

	public Object onActivate() throws Exception {
		if (!functionalityFacade.isEnableUploadRequest(userVo
				.getDomainIdentifier())) {
			return org.linagora.linshare.view.tapestry.pages.Index.class;
		}
		if (maxDepositSizeUnit == null) {
			maxDepositSizeUnit = FileSizeUnit.MEGA;
		}
		if (maxFileSizeUnit == null) {
			maxFileSizeUnit = FileSizeUnit.MEGA;
		}
		try {
			BeanModel<UploadRequestVo> model = beanModelSource.createEditModel(
					UploadRequestVo.class, messages);
			current = uploadRequestFacade.getDefaultValue(userVo, model);
			// initialize validators
			_d = current.getMaxDepositSize();
			_s = current.getMaxFileSize();
			_c = current.getMaxFileCount();
			_expiration = current.getExpiryDate();
			languageModel = new EnumSelectModel(Language.class, messages);
		} catch (BusinessException e) {
			logger.error("Cannot get default upload request value for user "
					+ userVo.getLsUuid());
			businessMessagesManagementService.notify(e);
			return Index.class;
		}
		return null;
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
		try {
			uploadRequestFacade.createRequest(userVo, current);
			return Index.class;
		} catch (BusinessException e) {
			businessMessagesManagementService.notify(e);
			return Create.class;
		}
	}

	@Log
	public Object onCanceled() throws BusinessException {
		return Index.class;
	}

	@Log
	public Object onValueChangedFromSelected(UploadRequestTemplateVo vo) {
		current.fromTemplate(vo);
		return reload;
	}

	public Long getMaxDepositSize() {
		return maxDepositSizeUnit.fromPlainSize(current.getMaxDepositSize().longValue());
	}

	public void setMaxDepositSize(Long maxDepositSize) {
		this.maxDepositSizeValue = maxDepositSize;
	}

	public FileSizeUnit getMaxDepositSizeUnit() {
		return maxDepositSizeUnit;
	}

	public void setMaxDepositSizeUnit(FileSizeUnit fileSizeUnit) {
		maxDepositSizeUnit = fileSizeUnit;
		current.setMaxDepositSize(maxDepositSizeUnit.getPlainSize(maxDepositSizeValue));
	}

	public Long getMaxFileSize() {
		return maxFileSizeUnit.fromPlainSize(current.getMaxFileSize().longValue());
	}

	public void setMaxFileSize(Long maxFileSize) {
		this.maxFileSizeValue = maxFileSize;
	}

	public FileSizeUnit getMaxFileSizeUnit() {
		return maxFileSizeUnit;
	}

	public void setMaxFileSizeUnit(FileSizeUnit fileSizeUnit) {
		maxFileSizeUnit = fileSizeUnit;
		current.setMaxFileSize(maxFileSizeUnit.getPlainSize(maxFileSizeValue));
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
	 * Dynamic validation
	 */

	public FieldValidator<?> getMaxFileCountValidator() {
		return source.createValidators(maxFileCount, "required, max=" + _c + ", min=1");
	}

	/*
	 * Models + ValueEncoder
	 */

	public BeanModel<UploadRequestVo> getRequestModel() {
		return current.getModel();
	}

	public List<UploadRequestTemplateVo> getSelectModel()
			throws BusinessException {
		return uploadRequestFacade.findAllTemplates(userVo);
	}

	public ValueEncoder<UploadRequestTemplateVo> getTemplateEncoder() {
		return new ValueEncoder<UploadRequestTemplateVo>() {
			@Override
			public String toClient(UploadRequestTemplateVo value) {
				return value.getUuid();
			}

			@Override
			public UploadRequestTemplateVo toValue(String uuid) {
				try {
					return uploadRequestFacade.findTemplateByUuid(userVo, uuid);
				} catch (BusinessException e) {
					logger.error("Could not find UploadRequestTemplate: "
							+ uuid);
					return null;
				}
			}
		};
	}

	/*
	 * Exception Handling
	 */

	public Object onException(Throwable cause) {
		shareSessionObjects.addError(messages.get("global.exception.message"));
		logger.error(cause.getMessage());
		cause.printStackTrace();
		return this;
	}
}
