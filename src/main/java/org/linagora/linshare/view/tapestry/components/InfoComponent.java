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
package org.linagora.linshare.view.tapestry.components;

import java.text.DateFormat;
import java.util.Locale;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.DocumentFacade;
import org.linagora.linshare.core.utils.FileUtils;
import org.slf4j.Logger;

public class InfoComponent {

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private AbstractDomainFacade domainFacade;

	@Inject
	private Messages messages;
	
	@Inject
	private Locale locale;
	
	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
	
	@SessionState
	@Property
	private UserVo userVo;
	
	@Property
	private boolean userVoExists;
	
	@Property
	private boolean isGuest;
	
	@Property
	private boolean canUpload;
	
	@Property
	private String expirationDate;
	
	@Property
	private String usedQuota;
	
	@Property
	private int usedQuotaPercent;
	
	@Property
	private String totalQuota;
	
	@Persist
	private DateFormat localisedDateFormat; // this formater is for the displayed date
	
	@Property
	private boolean globalQuota;
	
	@Inject
	private Logger logger;
	
	
	   /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
	
	@SetupRender
	void setupRender() throws BusinessException {
		if (getDisplayWidget() && userVo != null && userVo.getDomainIdentifier()!=null){
//			DomainVo domain = domainFacade.retrieveDomain(userVo.getDomainIdentifier());
			globalQuota = documentFacade.isGlobalQuotaActive(userVo);
			canUpload = userVo.isUpload();
			isGuest = userVo.isGuest();
	
			// the formater for the displayed date : we hide the timeline date, and add our date in the 
			// description
			localisedDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
			
			if (canUpload) {
	
				if (!globalQuota) {
					long userAvailableQuota = documentFacade.getUserAvailableQuota(userVo);
					long userTotalQuota = documentFacade.getUserTotalQuota(userVo);
					long userUsedQuota = userTotalQuota - userAvailableQuota;
					
					if(userUsedQuota<0) userUsedQuota = 0;
					if (userTotalQuota<1) {
						usedQuotaPercent = 0;
					} else {
						usedQuotaPercent = (int) (100*userUsedQuota / userTotalQuota);
						if(usedQuotaPercent>100) usedQuotaPercent = 100;
					}
					usedQuota = FileUtils.getFriendlySize(userUsedQuota, messages, FileUtils.getAppropriateUnitSize(userUsedQuota));
					totalQuota = FileUtils.getFriendlySize(userTotalQuota, messages, FileUtils.getAppropriateUnitSize(userTotalQuota));
				} else {
					Long usedQuotaB = domainFacade.getUsedSpace(userVo);
					Long globalQuotaB = documentFacade.getGlobalQuota(userVo);
					usedQuota = FileUtils.getFriendlySize(usedQuotaB, messages, FileUtils.getAppropriateUnitSize(usedQuotaB));
					totalQuota = FileUtils.getFriendlySize(globalQuotaB, messages, FileUtils.getAppropriateUnitSize(globalQuotaB));
					if (usedQuotaB<1) {
						usedQuotaPercent = 0;
					} else {
						usedQuotaPercent = (int) (100*usedQuotaB / globalQuotaB);
						if(usedQuotaPercent>100) usedQuotaPercent = 100;
					}
				}
			}
			
			if (isGuest) {
				if(userVo.getExpirationDate() != null)
					expirationDate = localisedDateFormat.format(userVo.getExpirationDate());
				else {
					logger.error("userVo.getExpirationDate() is null !");
					expirationDate = "Null";
				}
			}
		}
	}
	
	public boolean getDisplayWidget() {
		if (!userVoExists) {
			return false;
		}
		if (isGuest) {
			return true;
		}
		if (globalQuota) {
			return userVo.isAdministrator();
		}
		return true;
	}
	
	public boolean getDisplayUserQuota() {
		return !globalQuota;
	}
	
	public boolean getDisplayGlobalQuota() {
		return globalQuota && userVo.isAdministrator();
	}
	
	public boolean getDisplayQuota() {
		return (getDisplayUserQuota() && canUpload) || getDisplayGlobalQuota();
	}
	
	
	
}
