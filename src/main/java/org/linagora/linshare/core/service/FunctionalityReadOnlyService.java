/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

	public Functionality get(String domainIdentifier,String functionalityIdentifier);
	
	/** 
	 * Shortcuts to functionalities
	 */

	public SizeUnitValueFunctionality getGlobalQuotaFunctionality (AbstractDomain domain);
    public SizeUnitValueFunctionality getUserQuotaFunctionality (AbstractDomain domain);
    public SizeUnitValueFunctionality getUserMaxFileSizeFunctionality (AbstractDomain domain);
	public TimeUnitBooleanValueFunctionality getDefaultShareExpiryTimeFunctionality (AbstractDomain domain);
	public TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality (AbstractDomain domain);
	
	public Functionality getGuestFunctionality (AbstractDomain domain);
	public TimeUnitValueFunctionality getGuestAccountExpiryTimeFunctionality (AbstractDomain domain);
	
	
	public StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain);
	public StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain);
	public Functionality getMimeTypeFunctionality(AbstractDomain domain);
	public Functionality getEnciphermentFunctionality(AbstractDomain domain);
	public Functionality getAntivirusFunctionality(AbstractDomain domain);
	public Functionality getAnonymousUrlFunctionality(AbstractDomain domain);
	public Functionality getSecuredAnonymousUrlFunctionality(AbstractDomain domain);
	public Functionality getRestrictedGuestFunctionality(AbstractDomain domain);
	public Functionality getSignatureFunctionality(AbstractDomain domain);
	public Functionality getThreadCreationPermissionFunctionality(AbstractDomain domain);
	public Functionality getUpdateFilesFunctionality(AbstractDomain domain);
	public Functionality getUserCanUploadFunctionality(AbstractDomain domain);
	public StringValueFunctionality getCustomLogoFunctionality(AbstractDomain domain);
	public StringValueFunctionality getCustomLinkLogoFunctionality(AbstractDomain domain);
	public StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain);
	public StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain);
	
	public IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain);
	public Functionality getUserTabFunctionality(AbstractDomain domain);
	public Functionality getAuditTabFunctionality(AbstractDomain domain);
	public Functionality getThreadTabFunctionality(AbstractDomain domain);
	public Functionality getHelpTabFunctionality(AbstractDomain domain);
	public Functionality getListTabFunctionality(AbstractDomain domain);
	
	
	/**
	 * Check if SecuredAnonymousUrl (SAU) is mandatory
	 * @param domain : the current domain identifier
	 * @return 
	 */
	public boolean isSauMadatory(String domainIdentifier);
	
	/**
	 * Check if SecuredAnonymousUrl (SAU) is allowed
	 * @param domain : the current domain identifier
	 * @return 
	 */
	public boolean isSauAllowed(String domainIdentifier);

	/**
	 * return the default value for SecuredAnonymousUrl (SAU)
	 * @param domain : the current domain identifier
	 * @return
	 */
	public boolean getDefaultSauValue(String domainIdentifier);
	
	/**
	 * return the default value for RestrictedGuest
	 * @param domain : the current domain identifier
	 * @return
	 */
	public boolean getDefaultRestrictedGuestValue(String domainIdentifier);

	/**
	 * Check if RestrictedGuest is mandatory
	 * @param domain : the current domain identifier
	 * @return 
	 */
	public boolean isRestrictedGuestMadatory(String domainIdentifier);
	
	/**
	 * Check if RestrictedGuest is allowed
	 * @param domain : the current domain identifier
	 * @return 
	 */
	public boolean isRestrictedGuestAllowed(String domainIdentifier);

	/**
	 * Return the status of the custom logo  in root domain
	 * @return the status
	 */
	public boolean isCustomLogoActiveInRootDomain() throws BusinessException;
	
	/**
	 * Return the status of the custom logo  in root domain
	 * @return the status
	 */
	public String getCustomLogoUrlInRootDomain() throws BusinessException;
	
	/**
	 * Return the status of the custom link logo  in root domain
	 * @return the status
	 */
	public boolean isCustomLinkLogoActiveInRootDomain() throws BusinessException;
	
	/**
	 * Return the status of the custom link logo  in root domain
	 * @return the status
	 */
	public String getCustomLinkLogoInRootDomain() throws BusinessException;
	
	/**
	 * Return the status of the custom Notification URL in root domain
	 * @return the status
	 */
	public String getCustomNotificationURLInRootDomain() throws BusinessException;
	
}
