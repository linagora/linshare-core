/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;

public interface FunctionalityReadOnlyService {

	Functionality get(String domainIdentifier,String functionalityIdentifier);

	/** 
	 * Shortcuts to functionalities
	 */

	SizeUnitValueFunctionality getGlobalQuotaFunctionality (AbstractDomain domain);
    SizeUnitValueFunctionality getUserQuotaFunctionality (AbstractDomain domain);
    SizeUnitValueFunctionality getUserMaxFileSizeFunctionality (AbstractDomain domain);
	TimeUnitBooleanValueFunctionality getDefaultShareExpiryTimeFunctionality (AbstractDomain domain);
	TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality (AbstractDomain domain);

	Functionality getGuestFunctionality (AbstractDomain domain);
	TimeUnitValueFunctionality getGuestAccountExpiryTimeFunctionality (AbstractDomain domain);


	StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain);
	StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain);
	Functionality getMimeTypeFunctionality(AbstractDomain domain);
	Functionality getEnciphermentFunctionality(AbstractDomain domain);
	Functionality getAntivirusFunctionality(AbstractDomain domain);
	Functionality getAnonymousUrlFunctionality(AbstractDomain domain);
	Functionality getSecuredAnonymousUrlFunctionality(AbstractDomain domain);
	Functionality getRestrictedGuestFunctionality(AbstractDomain domain);
	Functionality getSignatureFunctionality(AbstractDomain domain);
	Functionality getThreadCreationPermissionFunctionality(AbstractDomain domain);
	Functionality getUpdateFilesFunctionality(AbstractDomain domain);
	Functionality getUserCanUploadFunctionality(AbstractDomain domain);
	StringValueFunctionality getCustomLogoFunctionality(AbstractDomain domain);
	StringValueFunctionality getCustomLinkLogoFunctionality(AbstractDomain domain);
	StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain);
	StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain);

	IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain);
	Functionality getUserTabFunctionality(AbstractDomain domain);
	Functionality getAuditTabFunctionality(AbstractDomain domain);
	Functionality getThreadTabFunctionality(AbstractDomain domain);
	Functionality getHelpTabFunctionality(AbstractDomain domain);
	Functionality getListTabFunctionality(AbstractDomain domain);

	// UPLOAD_REQUEST
	StringValueFunctionality getUploadRequestFunctionality(AbstractDomain domain);
	TimeUnitValueFunctionality getUploadRequestActivationTimeFunctionality (AbstractDomain domain);
	TimeUnitValueFunctionality getUploadRequestExpiryTimeFunctionality (AbstractDomain domain);
	Functionality getUploadRequestGroupedFunctionality(AbstractDomain domain);
	IntegerValueFunctionality getUploadRequestMaxFileCountFunctionality(AbstractDomain domain);
	SizeUnitValueFunctionality getUploadRequestMaxFileSizeFunctionality(AbstractDomain domain);
	SizeUnitValueFunctionality getUploadRequestMaxDepositSizeFunctionality(AbstractDomain domain);
	StringValueFunctionality getUploadRequestNotificationLanguageFunctionality(AbstractDomain domain);
	Functionality getUploadRequestSecureUrlFunctionality(AbstractDomain domain);
	Functionality getUploadRequestProlongationFunctionality(AbstractDomain domain);
	Functionality getUploadRequestDepositOnlyFunctionality(AbstractDomain domain);
	TimeUnitValueFunctionality getUploadRequestNotificationTimeFunctionality (AbstractDomain domain);

	// UPLOAD PROPOSITION
	Functionality getUploadPropositionFunctionality(AbstractDomain domain);

	/**
	 * Check if SecuredAnonymousUrl (SAU) is mandatory
	 * @param domain : the current domain identifier
	 * @return 
	 */
	boolean isSauMadatory(String domainIdentifier);

	/**
	 * Check if SecuredAnonymousUrl (SAU) is allowed
	 * @param domain : the current domain identifier
	 * @return 
	 */
	boolean isSauAllowed(String domainIdentifier);

	/**
	 * return the default value for SecuredAnonymousUrl (SAU)
	 * @param domain : the current domain identifier
	 * @return
	 */
	boolean getDefaultSauValue(String domainIdentifier);

	/**
	 * return the default value for RestrictedGuest
	 * @param domain : the current domain identifier
	 * @return
	 */
	boolean getDefaultRestrictedGuestValue(String domainIdentifier);

	/**
	 * Check if RestrictedGuest is mandatory
	 * @param domain : the current domain identifier
	 * @return 
	 */
	boolean isRestrictedGuestMadatory(String domainIdentifier);

	/**
	 * Check if RestrictedGuest is allowed
	 * @param domain : the current domain identifier
	 * @return 
	 */
	boolean isRestrictedGuestAllowed(String domainIdentifier);

	/**
	 * Return the status of the custom logo  in root domain
	 * @return the status
	 */
	boolean isCustomLogoActiveInRootDomain() throws BusinessException;

	/**
	 * Return the status of the custom logo  in root domain
	 * @return the status
	 */
	String getCustomLogoUrlInRootDomain() throws BusinessException;

	/**
	 * Return the status of the custom link logo  in root domain
	 * @return the status
	 */
	boolean isCustomLinkLogoActiveInRootDomain() throws BusinessException;

	/**
	 * Return the status of the custom link logo  in root domain
	 * @return the status
	 */
	String getCustomLinkLogoInRootDomain() throws BusinessException;

	/**
	 * Return the status of the custom Notification URL in root domain
	 * @return the status
	 */
	String getCustomNotificationURLInRootDomain() throws BusinessException;

}
