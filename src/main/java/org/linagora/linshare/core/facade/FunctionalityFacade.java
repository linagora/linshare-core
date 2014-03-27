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
package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.vo.FunctionalityVo;
import org.linagora.linshare.core.domain.vo.PolicyVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface FunctionalityFacade {

	public List<PolicyVo> getAllActivationPolicy(String domainIdentifier);
	public List<PolicyVo> getAllConfigurationPolicy(String domainIdentifier);
	public List<FunctionalityVo> getAllParameters(String domainIdentifier);
	
	public void updateActivationPolicies(UserVo actorVo, List<PolicyVo> policies) throws BusinessException;
	public void updateConfigurationPolicies(UserVo actorVo, List<PolicyVo> policies) throws BusinessException;
	public void updateParameters(UserVo actorVo, List<FunctionalityVo> functionalities) throws BusinessException;
	
	public Integer completionThreshold(String domainIdentifier);
	public boolean isEnableUserTab(String domainIdentifier);
	public boolean isEnableAuditTab(String domainIdentifier);
	public boolean isEnableHelpTab(String domainIdentifier);
	public boolean isEnableListTab(String domainIdentifier);
	public boolean isEnableThreadTab(String domainIdentifier);
	public boolean isEnableUpdateFiles(String domainIdentifier);
	public boolean isEnableCreateThread(String domainIdentifier);
	public boolean isEnableCustomLogoLink(String domainIdentifier);
	
	public boolean getDefaultRestrictedGuestValue(String domainIdentifier);
	public boolean isRestrictedGuestEnabled(String domainIdentifier);
	
	/**
	 * Check if the policy "Allowed guest" is enabled
	 * 
	 * @param domainIdentifier the domain to check
	 * @return return true if the policy is enabled, otherwise return false
	 */
	public boolean isEnableGuest(String domainIdentifier);
}
