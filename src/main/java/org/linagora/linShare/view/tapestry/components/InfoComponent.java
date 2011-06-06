/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.components;

import java.text.DateFormat;
import java.util.Locale;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.DocumentFacade;
import org.linagora.linShare.core.Facade.DomainFacade;
import org.linagora.linShare.core.domain.vo.DomainVo;
import org.linagora.linShare.core.domain.vo.ParameterVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.utils.FileUtils;

public class InfoComponent {

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */
	
	@Inject
	private DocumentFacade documentFacade;

	@Inject
	private DomainFacade domainFacade;

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
	
	@SuppressWarnings("unused")
	@Property
	private String expirationDate;
	
	@SuppressWarnings("unused")
	@Property
	private String usedQuota;
	
	@Property
	private int usedQuotaPercent;
	
	@SuppressWarnings("unused")
	@Property
	private String totalQuota;
	
	@Persist
	private DateFormat localisedDateFormat; // this formater is for the displayed date
	
	@Property
	private boolean globalQuota;
	
	   /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
	
	
	@SetupRender
	void setupRender() throws BusinessException {
		if (getDisplayWidget() && userVo != null && userVo.getDomainIdentifier()!=null){
			DomainVo domain = domainFacade.retrieveDomain(userVo.getDomainIdentifier());
			ParameterVo param = domain.getParameterVo();
			globalQuota = param.getGlobalQuotaActive();
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
					
					FileUtils.Unit preferedUnity= FileUtils.getAppropriateUnitSize(userTotalQuota);
					usedQuota = FileUtils.getFriendlySize(userUsedQuota, messages, preferedUnity);
					totalQuota = FileUtils.getFriendlySize(userTotalQuota, messages, preferedUnity);
				} else {
					Long usedQuotaB = param.getUsedQuota() == null ? 0L : param.getUsedQuota();
					Long globalQuotaB = param.getGlobalQuota() == null ? 0L : param.getGlobalQuota();
					FileUtils.Unit preferedUnity= FileUtils.getAppropriateUnitSize(globalQuotaB);
					usedQuota = FileUtils.getFriendlySize(usedQuotaB, messages, preferedUnity);
					totalQuota = FileUtils.getFriendlySize(globalQuotaB, messages, preferedUnity);
					if (usedQuotaB<1) {
						usedQuotaPercent = 0;
					} else {
						usedQuotaPercent = (int) (100*usedQuotaB / globalQuotaB);
						if(usedQuotaPercent>100) usedQuotaPercent = 100;
					}
				}
			}
			
			if (isGuest) {
				expirationDate = localisedDateFormat.format(userVo.getExpirationDate());
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
