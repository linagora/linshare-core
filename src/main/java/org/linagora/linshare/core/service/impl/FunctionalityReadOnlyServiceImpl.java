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
package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityReadOnlyServiceImpl implements
		FunctionalityReadOnlyService {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityReadOnlyServiceImpl.class);

	private final DomainBusinessService domainBusinessService;

	private final FunctionalityRepository functionalityRepository;

	public FunctionalityReadOnlyServiceImpl(
			DomainBusinessService domainBusinessService,
			FunctionalityRepository functionalityRepository) {
		super();
		this.domainBusinessService = domainBusinessService;
		this.functionalityRepository = functionalityRepository;
	}


	private AbstractDomain getRootDomain() throws BusinessException {
		return domainBusinessService.getUniqueRootDomain();
	}

	@Override
	public Functionality get(String domainIdentifier, String functionalityIdentifier) throws BusinessException {
		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
		Functionality functionality = _getFunctionality(domain, functionalityIdentifier);
		// Always return a read only functionality.
		return (Functionality)functionality.clone();
	}

	/**
	 * This method should not be used except by the FunctionalityReadOnlyServiceImpl.get method.
	 * @param domain
	 * @param functionalityIdentifier
	 * @return
	 */
	private  Functionality _getFunctionality(AbstractDomain domain, String functionalityIdentifier) {
		Functionality fonc = functionalityRepository.findByDomain(domain, functionalityIdentifier);
		if (fonc == null && domain.getParentDomain() != null) {
			fonc = _getFunctionality(domain.getParentDomain(), functionalityIdentifier);
		}
		return fonc;
	}

	/*
	 * Adapter
	 */
	private  Functionality _getFunctionality(AbstractDomain domain, FunctionalityNames fn) {
		return _getFunctionality(domain, fn.toString());
	}

	@Override
	public TimeUnitValueFunctionality getDefaultShareExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_EXPIRATION));
	}

	@Override
	public BooleanValueFunctionality getDefaultShareExpiryTimeDeletionFunctionality(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION);
	}

	@Override
	public TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.DOCUMENT_EXPIRATION));
	}

	@Override
	public Calendar getDefaultFileExpiryTime(AbstractDomain domain) {
		TimeUnitValueFunctionality fileExpirationTimeFunctionality = getDefaultFileExpiryTimeFunctionality(domain);
		if (fileExpirationTimeFunctionality.getActivationPolicy().getStatus()) {
			Calendar expirationDate = Calendar.getInstance();
			expirationDate.add(fileExpirationTimeFunctionality.toCalendarValue(), fileExpirationTimeFunctionality.getMaxValue());
			return expirationDate;
		}
		return null;
	}

	@Override
	public Functionality getGuests(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS);
	}

	@Override
	public BooleanValueFunctionality getGuestsRestricted(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.GUESTS__RESTRICTED);
	}

	@Override
	public BooleanValueFunctionality getGuestsCanUpload(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.GUESTS__CAN_UPLOAD);
	}

	@Override
	public TimeUnitValueFunctionality getGuestsExpiration(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.GUESTS__EXPIRATION));
	}

	@Override
	public Functionality getGuestsExpirationDateProlongation(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS__EXPIRATION_ALLOW_PROLONGATION);
	}

	@Override
	public StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.TIME_STAMPING);
	}

	@Override
	public StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.DOMAIN__MAIL);
	}

	@Override
	public Functionality getMimeTypeFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.MIME_TYPE);
	}

	@Override
	public Functionality getEnciphermentFunctionality(AbstractDomain domain) {
		Functionality functionality = _getFunctionality(domain, FunctionalityNames.ENCIPHERMENT);
		functionality = getForbiddenFunctionnality(functionality);
		return functionality;
	}

	@Override
	public Functionality getAntivirusFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.ANTIVIRUS);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrl(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrlNotification(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL__NOTIFICATION);
	}

	@Override
	public StringValueFunctionality getAnonymousURLNotificationUrl(AbstractDomain domain) {
		return (StringValueFunctionality)_getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL__NOTIFICATION_URL);
	}

	@Override
	public BooleanValueFunctionality getAcknowledgement(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER);
	}

	@Override
	public BooleanValueFunctionality getUndownloadedSharedDocumentsAlert(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT);
	}

	@Override
	public IntegerValueFunctionality getUndownloadedSharedDocumentsAlertDuration(AbstractDomain domain) {
		return (IntegerValueFunctionality) _getFunctionality(domain, FunctionalityNames.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrl(String domainIdentifier) {
		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
		return getAnonymousUrl(domain);
	}

	@Override
	public Functionality getRestrictedGuestFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS__RESTRICTED);
	}

	@Override
	public Functionality getUserCanUploadFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.INTERNAL_CAN_UPLOAD);
	}

	@Override
	public IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain) {
		return (IntegerValueFunctionality) _getFunctionality(domain, FunctionalityNames.COMPLETION);
	}

	@Override
	public Functionality getWorkGroupCreationRight(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.WORK_GROUP__CREATION_RIGHT);
	}

	@Override
	public Functionality getWorkGroupFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.WORK_GROUP);
	}

	@Override
	public BooleanValueFunctionality getWorkGroupFileVersioning(AbstractDomain domain) {
		return (BooleanValueFunctionality) _getFunctionality(domain, FunctionalityNames.WORK_GROUP__FILE_VERSIONING);
	}

	@Override
	public StringValueFunctionality getWorkGroupFileEdition(AbstractDomain domain) {
		return (StringValueFunctionality)_getFunctionality(domain, FunctionalityNames.WORK_GROUP__FILE_EDITION);
	}

	@Override
	public SizeUnitValueFunctionality getWorkGoupDownloadArchive(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.WORK_GROUP__DOWNLOAD_ARCHIVE));
	}
	
	@Override
	public Functionality getContactsListFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.CONTACTS_LIST);
	}

	@Override
	public Functionality getContactsListCreationFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.CONTACTS_LIST__CREATION_RIGHT);
	}

	@Override
	public StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.SHARE_NOTIFICATION_BEFORE_EXPIRATION);
	}

	@Override
	public StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.DOMAIN__NOTIFICATION_URL);
	}

	@Override
	public StringValueFunctionality getCustomNotificationUrlForExternalsFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL__NOTIFICATION_URL);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrlForceAnonymousSharing(AbstractDomain domain) {
		return (BooleanValueFunctionality) _getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL__FORCE_ANONYMOUS_SHARING);
	}

	@Override
	public StringValueFunctionality getUploadRequestFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST);
	}

	@Override
	public TimeUnitValueFunctionality getUploadRequestActivationTimeFunctionality(
			AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION));
	}

	@Override
	public TimeUnitValueFunctionality getUploadRequestExpiryTimeFunctionality(
			AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION));
	}

	@Override
	public IntegerValueFunctionality getUploadRequestMaxFileCountFunctionality(
			AbstractDomain domain) {
		return (IntegerValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_FILE_COUNT);
	}

	@Override
	public SizeUnitValueFunctionality getUploadRequestMaxFileSizeFunctionality(
			AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_FILE_SIZE));
	}

	@Override
	public SizeUnitValueFunctionality getUploadRequestMaxDepositSizeFunctionality(
			AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE));
	}

	@Override
	public LanguageEnumValueFunctionality getUploadRequestNotificationLanguageFunctionality(
			AbstractDomain domain) {
		return (LanguageEnumValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__NOTIFICATION_LANGUAGE);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestSecureUrlFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__SECURED_URL);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestCanCloseFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__CAN_CLOSE);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestCandDeleteFileFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__CAN_DELETE);
	}

	@Override
	public Functionality getJwtLongTimeFunctionalityForUser(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.JWT_PERMANENT_TOKEN__USER_MANAGEMENT);
	}

	@Override
	public Functionality getJwtLongTimeFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.JWT_PERMANENT_TOKEN);
	}

	@Override
	public Functionality getJwtLongTimeFunctionality(String domainUuid) {
		AbstractDomain domain = domainBusinessService.find(domainUuid);
		return getJwtLongTimeFunctionality(domain);
	}

	@Override
	public TimeUnitValueFunctionality getUploadRequestNotificationTimeFunctionality(
			AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION));
	}

	@Override
	public Functionality getUploadRequestEnableTemplateFunctionality(
			AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST_ENABLE_TEMPLATE);
	}

	private Functionality getForbiddenFunctionnality(Functionality functionality) {
		if (functionality == null) {
			Policy activation = new Policy(Policies.FORBIDDEN, false, true);
			functionality = new Functionality();
			functionality.setActivationPolicy(activation);
			functionality.setConfigurationPolicy(activation);
		}
		return functionality;
	}

	@Override
	public String getCustomNotificationURLInRootDomain() throws BusinessException {
		return this.getCustomNotificationUrlFunctionality(getRootDomain()).getValue();
	}

	@Override
	public BooleanValueFunctionality getAcknowledgement(String domainIdentifier) {
		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
		return getAcknowledgement(domain);
	}

	@Override
	public Functionality getDriveFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.DRIVE);
	}

	@Override
	public Functionality getDriveCreationRight(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.DRIVE__CREATION_RIGHT);
	}


	@Override
	public BooleanValueFunctionality getSecondFactorAuthenticationFunctionality(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.SECOND_FACTOR_AUTHENTICATION);
	}

	@Override
	public Integer getIntegerValue(IntegerValueFunctionality func, Integer currentInteger,
			BusinessErrorCode errorCode) {
		if (!func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is not activated");
			return null;
		}
		Integer defaultInteger = func.getValue();
		if (func.getDelegationPolicy() == null || !func.getDelegationPolicy().getStatus() || currentInteger == null) {
			debuggerInteger(func, currentInteger, defaultInteger);
			return defaultInteger;
		}
		logger.debug(func.getIdentifier() + " has a delegation policy");
		Integer maxInteger = func.getMaxValue();
		if (currentInteger > maxInteger) {
			logger.warn("the current value " + currentInteger.toString() + " is out of range : " + func.toString());
			throw new BusinessException(errorCode,
					"the current value " + currentInteger.toString() + " is out of range : " + func.toString());
		}
		return currentInteger;
	}

	/**
	 * Check if the current input date is after now and not before now more the
	 * functionality duration if delegation policy allowed it. now() < currentDate <
	 * now() + func.value Otherwise functionality value is used as default value.
	 * 
	 * @param func
	 * @param currentDate
	 * @return the proper date is returned according to activation policy,
	 *         configuration policy and others checks.
	 */
	@Override
	public Date getDateValue(TimeUnitValueFunctionality func, Date currentDate, BusinessErrorCode errorCode) {
		if (!func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is not activated");
			return null;
		}
		logger.debug(func.getIdentifier() + " is activated");
		Calendar calendar = getCalendarWithoutTime(new Date());
		calendar.add(func.toCalendarValue(), func.getValue());
		Date defaultDate = calendar.getTime();
		if (func.getDelegationPolicy() == null || !func.getDelegationPolicy().getStatus() || currentDate == null) {
			debuggerTime(func, currentDate, defaultDate);
			return defaultDate;
		}
		logger.debug(func.getIdentifier() + " has a delegation policy");
		// check if there is limitation of maximum value
		// -1 mean no limit
		Date now = getCalendarWithoutTime(new Date()).getTime();
		if (func.getMaxValue() == -1 && (currentDate.after(now) || currentDate.equals(now))) {
			return currentDate;
		}
		Calendar c = new GregorianCalendar();
		c.add(func.toCalendarMaxValue(), func.getMaxValue());
		Date maxDate = c.getTime(); // Maximum value allowed
		if (currentDate.before(now) || currentDate.after(maxDate)) {
			logger.warn("the current value " + currentDate.toString() + " is out of range : " + func.toString() + " : "
					+ maxDate.toString());
			throw new BusinessException(errorCode, "the current value " + currentDate.toString() + " is out of range : "
					+ func.toString() + " : " + maxDate.toString());
		}
		return currentDate;
	}

	@Override
	public Long getSizeValue(SizeUnitValueFunctionality func, Long currentSize, BusinessErrorCode errorCode) {
		if (!func.getActivationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " is not activated");
			return null;
		}
		logger.debug(func.getIdentifier() + " is activated");
		Long defaultSize = ((FileSizeUnitClass) func.getUnit()).getPlainSize(func.getValue());
		if (func.getDelegationPolicy() == null || !func.getDelegationPolicy().getStatus() || currentSize == null) {
			debuggerSize(func, currentSize, defaultSize);
			return defaultSize;
		}
		logger.debug(func.getIdentifier() + " has a delegation policy");
		Long maxSize = ((FileSizeUnitClass) func.getMaxUnit()).getPlainSize(func.getMaxValue());
		if (currentSize > maxSize) {
			logger.warn("the current value " + currentSize.toString() + " is out of range : " + func.toString());
			throw new BusinessException(errorCode,
					"the current value " + currentSize.toString() + " is out of range : " + func.toString());
		}
		return currentSize;
	}

	/**
	 * Check if the current input date is after now and not before now more the
	 * functionality duration if delegation policy allowed it. now() < currentDate <
	 * now() + func.value Otherwise functionality value is used as default value.
	 * 
	 * @param func
	 * @param currentDate
	 * @param expirationDate TODO
	 * @return the proper date is returned according to activation policy,
	 *         configuration policy and others checks.
	 */
	@Override
	public Date getNotificationDateValue(TimeUnitValueFunctionality func, Date currentDate, Date expirationDate,
			BusinessErrorCode errorCode) {
		if (!func.getActivationPolicy().getStatus() || expirationDate == null) {
			logger.debug(func.getIdentifier() + " is not activated");
			return null;
		}
		logger.debug(func.getIdentifier() + " is activated");
		Date now = getCalendarWithoutTime(new Date()).getTime();
		Calendar c = getCalendarWithoutTime(expirationDate);
		c.add(func.toCalendarValue(), -func.getValue());
		Date defaultDate = c.getTime();
		if (defaultDate.before(now)) {
			defaultDate = now;
		}
		if (func.getDelegationPolicy() == null || !func.getDelegationPolicy().getStatus() || currentDate == null) {
			debuggerTime(func, currentDate, defaultDate);
			return defaultDate;
		}
		logger.debug(func.getIdentifier() + " has a delegation policy");
		Calendar cal = getCalendarWithoutTime(expirationDate);
		cal.add(func.toCalendarMaxValue(), -func.getMaxValue());
		Date minDate = cal.getTime(); // notification should be between min date and expiration date
		if (minDate.before(now)) {
			minDate = now;
		}
		if (currentDate.after(expirationDate) || currentDate.before(minDate)) {
			logger.warn("the current value " + currentDate.toString() + " is out of range : " + func.toString());
			throw new BusinessException(errorCode,
					"the current value " + currentDate.toString() + " is out of range : " + func.toString());
		}
		return currentDate;
	}

	private void debuggerInteger(IntegerValueFunctionality func, Integer currentInteger, Integer defaultInteger) {
		if (func.getDelegationPolicy() == null) {
			logger.debug(func.getIdentifier() + " does not have a delegation policy");
		}
		if (!func.getDelegationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " does not allow delegation policy ");
		}
		if (currentInteger == null) {
			logger.debug("The given integer value is null, default integer value will be used " + defaultInteger);
		}
	}

	private void debuggerTime(TimeUnitValueFunctionality func, Date currentDate, Date defaultDate) {
		if (func.getDelegationPolicy() == null) {
			logger.debug(func.getIdentifier() + " does not have a delegation policy");
		}
		if (!func.getDelegationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " does not allow delegation policy ");
		}
		if (currentDate == null) {
			logger.debug("The given date value is null, default date value will be used " + defaultDate);
		}
	}

	private void debuggerSize(SizeUnitValueFunctionality func, Long currentSize, Long defaultValue) {
		if (func.getDelegationPolicy() == null) {
			logger.debug(func.getIdentifier() + " does not have a delegation policy");
		}
		if (!func.getDelegationPolicy().getStatus()) {
			logger.debug(func.getIdentifier() + " does not allow delegation policy ");
		}
		if (currentSize == null) {
			logger.debug("The given size value is null, default size value will be used " + defaultValue);
		}
	}

	private Calendar getCalendarWithoutTime(Date date) {
		Calendar calendar = new GregorianCalendar();
		if (date != null) {
			calendar.setTime(date);
		}
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

}
