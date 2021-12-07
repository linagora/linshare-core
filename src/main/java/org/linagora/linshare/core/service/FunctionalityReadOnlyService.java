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
package org.linagora.linshare.core.service;

import java.util.Calendar;
import java.util.Date;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

public interface FunctionalityReadOnlyService {

	Functionality get(String domainIdentifier,String functionalityIdentifier) throws BusinessException;

	/** 
	 * Shortcuts to functionalities
	 */

	TimeUnitValueFunctionality getDefaultShareExpiryTimeFunctionality (AbstractDomain domain);
	BooleanValueFunctionality getDefaultShareExpiryTimeDeletionFunctionality (AbstractDomain domain);
	TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality (AbstractDomain domain);
	Calendar getDefaultFileExpiryTime(AbstractDomain domain);

	Functionality getGuests (AbstractDomain domain);
	TimeUnitValueFunctionality getGuestsExpiration (AbstractDomain domain);
	BooleanValueFunctionality getGuestsRestricted(AbstractDomain domain);
	BooleanValueFunctionality getGuestsCanUpload(AbstractDomain domain);
	Functionality getGuestsExpirationDateProlongation(AbstractDomain domain);

	StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain);
	StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain);
	Functionality getMimeTypeFunctionality(AbstractDomain domain);
	Functionality getEnciphermentFunctionality(AbstractDomain domain);
	Functionality getAntivirusFunctionality(AbstractDomain domain);
	BooleanValueFunctionality getAnonymousUrl(AbstractDomain domain);
	BooleanValueFunctionality getAnonymousUrlNotification(AbstractDomain domain);
	BooleanValueFunctionality getAnonymousUrl(String domainIdentifier);
	BooleanValueFunctionality getAnonymousUrlForceAnonymousSharing(AbstractDomain domain);
	StringValueFunctionality getAnonymousURLNotificationUrl(AbstractDomain domain);
	Functionality getRestrictedGuestFunctionality(AbstractDomain domain);
	Functionality getWorkGroupCreationRight(AbstractDomain domain);
	BooleanValueFunctionality getWorkGroupFileVersioning(AbstractDomain domain);
	StringValueFunctionality getWorkGroupFileEdition(AbstractDomain domain);
	SizeUnitValueFunctionality getWorkGoupDownloadArchive(AbstractDomain domain);
	
	BooleanValueFunctionality getEnableInternalPersonalSpaceFunctionality(AbstractDomain domain);
	StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain);
	StringValueFunctionality getCustomNotificationUrlForExternalsFunctionality(AbstractDomain domain);
	StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain);
	BooleanValueFunctionality getAcknowledgement(String domainIdentifier);
	BooleanValueFunctionality getAcknowledgement(AbstractDomain domain);
	BooleanValueFunctionality getUndownloadedSharedDocumentsAlert(AbstractDomain domain);
	IntegerValueFunctionality getUndownloadedSharedDocumentsAlertDuration(AbstractDomain domain);

	IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain);
	Functionality getContactsListFunctionality(AbstractDomain domain);
	Functionality getContactsListCreationFunctionality(AbstractDomain domain);

	// UPLOAD_REQUEST
	StringValueFunctionality getUploadRequestFunctionality(AbstractDomain domain);
	TimeUnitValueFunctionality getUploadRequestActivationTimeFunctionality(AbstractDomain domain);
	TimeUnitValueFunctionality getUploadRequestExpiryTimeFunctionality(AbstractDomain domain);
	IntegerValueFunctionality getUploadRequestMaxFileCountFunctionality(AbstractDomain domain);
	SizeUnitValueFunctionality getUploadRequestMaxFileSizeFunctionality(AbstractDomain domain);
	SizeUnitValueFunctionality getUploadRequestMaxDepositSizeFunctionality(AbstractDomain domain);
	LanguageEnumValueFunctionality getUploadRequestNotificationLanguageFunctionality(AbstractDomain domain);
	BooleanValueFunctionality getUploadRequestSecureUrlFunctionality(AbstractDomain domain);
	BooleanValueFunctionality getUploadRequestCandDeleteFileFunctionality(AbstractDomain domain);
	BooleanValueFunctionality getUploadRequestCanCloseFunctionality(AbstractDomain domain);
	TimeUnitValueFunctionality getUploadRequestNotificationTimeFunctionality(AbstractDomain domain);
	Functionality getUploadRequestEnableTemplateFunctionality(AbstractDomain domain);
	Functionality getUploadRequestReminderNotificationFunctionality(AbstractDomain domain);

	/**
	 * Return the status of the custom Notification URL in root domain
	 * @return the status
	 */
	String getCustomNotificationURLInRootDomain() throws BusinessException;

	BooleanValueFunctionality getJwtLongTimeFunctionality(AbstractDomain domain);

	BooleanValueFunctionality getJwtLongTimeFunctionality(String domainuuid);

	//Drive Functionnality
	Functionality getDriveCreationRight(AbstractDomain domain);

	Functionality getSharedSpaceFunctionality(AbstractDomain domain);

	BooleanValueFunctionality getSecondFactorAuthenticationFunctionality(AbstractDomain domain);

	Integer getIntegerValue(IntegerValueFunctionality func, Integer maxFileCount, BusinessErrorCode errorCode);

	Date getDateValue(TimeUnitValueFunctionality func, Date currentDate, BusinessErrorCode errorCode);

	Date getUploadRequestDateValue(TimeUnitValueFunctionality func, Date currentDate, BusinessErrorCode errorCode);

	Long getSizeValue(SizeUnitValueFunctionality func, Long currentSize, BusinessErrorCode errorCode);

	Date getUploadRequestExpirationDateValue(TimeUnitValueFunctionality func, Date currentDate, Date activationDate,
			BusinessErrorCode errorCode);

	Date getNotificationDateValue(TimeUnitValueFunctionality func, Date currentDate, Date expirationDate, BusinessErrorCode errorCode);

	Date roundToUpperHour(Date dateToRound);

	Calendar getCalendarWithoutTime(Date date);

	TimeUnitValueFunctionality getCollectedEmailsExpirationTimeFunctionality(AbstractDomain domain);
}
