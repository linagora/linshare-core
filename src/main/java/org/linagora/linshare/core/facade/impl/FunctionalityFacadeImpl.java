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
package org.linagora.linshare.core.facade.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.UnitType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.TimeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.vo.FunctionalityVo;
import org.linagora.linshare.core.domain.vo.IntegerValueFunctionalityVo;
import org.linagora.linshare.core.domain.vo.PolicyVo;
import org.linagora.linshare.core.domain.vo.SizeValueFunctionalityVo;
import org.linagora.linshare.core.domain.vo.StringValueFunctionalityVo;
import org.linagora.linshare.core.domain.vo.TimeValueBooleanFunctionalityVo;
import org.linagora.linshare.core.domain.vo.TimeValueFunctionalityVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityOldService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityFacadeImpl implements FunctionalityFacade {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityFacadeImpl.class);
	
	private final FunctionalityOldService functionalityService;
	
	private final FunctionalityReadOnlyService functionalityReadOnlyService;
	
	private final AbstractDomainService abstractDomainService;
	
	
	public FunctionalityFacadeImpl(FunctionalityOldService functionalityService,
			AbstractDomainService abstractDomainService,
			FunctionalityReadOnlyService functionalityReadOnlyService) {
		super();
		this.functionalityService = functionalityService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
	}
	

	@Override
	public List<PolicyVo> getAllActivationPolicy(String domainIdentifier) {
		List<PolicyVo> res = new ArrayList<PolicyVo>();
		List<Functionality> list = functionalityService.getAllAvailableFunctionalities(domainIdentifier);
		logger.debug("AllAvailableFunctionalities : " + list.size());
		for (Functionality functionality : list) {
			res.add(new PolicyVo(functionality.getActivationPolicy(), functionality.getIdentifier(), domainIdentifier));
		}
		
		return res;
	}
	

	@Override
	public List<PolicyVo> getAllConfigurationPolicy(String domainIdentifier) {
		List<PolicyVo> res = new ArrayList<PolicyVo>();
		List<Functionality> list = functionalityService.getAllAlterableFunctionalities(domainIdentifier);
		logger.debug("AllAlterableFunctionalities : " + list.size());
		for (Functionality functionality : list) {
			res.add(new PolicyVo(functionality.getConfigurationPolicy(), functionality.getIdentifier(), domainIdentifier));
		}
		
		return res;
	}
	
	
	@Override
	public List<FunctionalityVo> getAllParameters(String domainIdentifier) {
		List<FunctionalityVo> res = new ArrayList<FunctionalityVo>();
		List<Functionality> list = functionalityService.getAllEditableFunctionalities(domainIdentifier);
		logger.debug("AllEditableFunctionalities : " + list.size());
		for (Functionality functionality : list) {
			if(functionality.getType().equals(FunctionalityType.INTEGER)) {
				IntegerValueFunctionality f = (IntegerValueFunctionality)functionality;
				res.add(new IntegerValueFunctionalityVo(functionality.getIdentifier(), domainIdentifier, f.getValue()));
				
			} else if(functionality.getType().equals(FunctionalityType.STRING)) {
				StringValueFunctionality f = (StringValueFunctionality)functionality;
				res.add(new StringValueFunctionalityVo(functionality.getIdentifier(), domainIdentifier, f.getValue()));
				
			} else if(functionality.getType().equals(FunctionalityType.UNIT)) {
				UnitValueFunctionality f = (UnitValueFunctionality)functionality;
				
				if(f.getUnit().getUnitType().equals(UnitType.TIME)) {
					TimeUnitClass timeUnit = (TimeUnitClass)f.getUnit();
					res.add(new TimeValueFunctionalityVo(functionality.getIdentifier(), domainIdentifier, f.getValue(), timeUnit.getUnitValue()));
					
				} else if(f.getUnit().getUnitType().equals(UnitType.SIZE)) {
					FileSizeUnitClass sizeUnit = (FileSizeUnitClass)f.getUnit();
					res.add(new SizeValueFunctionalityVo(functionality.getIdentifier(), domainIdentifier, f.getValue(), sizeUnit.getUnitValue()));
					
				} else {
					logger.error("Unknown Unit Functionality Type for : " + functionality.getIdentifier());
				}
			} else if(functionality.getType().equals(FunctionalityType.UNIT_BOOLEAN_TIME)) {
				UnitBooleanValueFunctionality f = (UnitBooleanValueFunctionality)functionality;
				
				if(f.getUnit().getUnitType().equals(UnitType.TIME)) {
					TimeUnitClass timeUnit = (TimeUnitClass)f.getUnit();
					res.add(new TimeValueBooleanFunctionalityVo(functionality.getIdentifier(), domainIdentifier, f.getValue(), timeUnit.getUnitValue(), f.getBool()));
				} else {
					logger.error("Unknown Unit boolean Functionality Type for : " + functionality.getIdentifier());
				}
			} else {
				logger.error("Unknown Functionality Type for : " + functionality.getIdentifier() + " : " + functionality.getType().toString());
			}
		}
		
		return res;
	}

	
	@Override
	public void updateActivationPolicies(UserVo actorVo, List<PolicyVo> policies) throws BusinessException {
		if(isAuthorized(actorVo)) {
			for (PolicyVo policyVo : policies) {
				logger.debug("Domain: " + policyVo.getDomainIdentifier() + " : " + "Func: " + policyVo.getFunctionalityIdentifier() + " : " + policyVo.getPolicy() + " : status : " + policyVo.getStatus());
				Functionality f = functionalityService.getFunctionalityByIdentifiers(policyVo.getDomainIdentifier(), policyVo.getFunctionalityIdentifier());
				f.getActivationPolicy().setPolicy(policyVo.getPolicy());
				f.getActivationPolicy().setStatus(policyVo.getStatus());
				functionalityService.update(policyVo.getDomainIdentifier(), f);
			}
		}
	}
	
	
	@Override
	public void updateConfigurationPolicies(UserVo actorVo, List<PolicyVo> policies) throws BusinessException {
		if(isAuthorized(actorVo)) {
			for (PolicyVo policyVo : policies) {
				logger.debug("Domain: " + policyVo.getDomainIdentifier() + " : " + "Func: " + policyVo.getFunctionalityIdentifier() + " : " + policyVo.getPolicy() + " : status : " + policyVo.getStatus());
				Functionality f = functionalityService.getFunctionalityByIdentifiers(policyVo.getDomainIdentifier(), policyVo.getFunctionalityIdentifier());
				f.getConfigurationPolicy().setPolicy(policyVo.getPolicy());
				f.getConfigurationPolicy().setStatus(policyVo.getStatus());
				functionalityService.update(policyVo.getDomainIdentifier(), f);
			}
		}
	}
	
	
	@Override
	public void updateParameters(UserVo actorVo, List<FunctionalityVo> functionalities) throws BusinessException {
		if(isAuthorized(actorVo)) {
			for (FunctionalityVo functionalityVo : functionalities) {
				Functionality entity = functionalityService.getFunctionalityByIdentifiers(functionalityVo.getDomainIdentifier(), functionalityVo.getIdentifier());
				entity.updateFunctionalityValuesOnlyFromVo(functionalityVo);
				functionalityService.update(functionalityVo.getDomainIdentifier(), entity);
			}
		}
	}
	
	
	private boolean isAuthorized(UserVo actorVo) {
		if(actorVo !=null) {
			if (actorVo.isSuperAdmin() || actorVo.isAdministrator()) {
				return true;
			}
			logger.error("you are not authorised.");
		} else {
			logger.error("isAuthorized:actorVo object is null.");
		}
		return false;
	}

	
	@Override
	public Integer completionThreshold(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			IntegerValueFunctionality completionFunctionality = functionalityReadOnlyService.getCompletionFunctionality(domain);
			if(completionFunctionality.getActivationPolicy().getStatus()) {
				return completionFunctionality.getValue();
			}
		} catch (BusinessException e) {
			logger.error("Can't find completion functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return LinShareConstants.completionThresholdConstantForDeactivation;
	}

	
	@Override
	public boolean isEnableUserTab(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality userTabFunctionality = functionalityReadOnlyService.getUserTabFunctionality(domain);
			return userTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find user tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	
	@Override
	public boolean isEnableAuditTab(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality auditTabFunctionality = functionalityReadOnlyService.getAuditTabFunctionality(domain);
			return auditTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find audit tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	
	@Override
	public boolean isEnableThreadTab(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality threadTabFunctionality = functionalityReadOnlyService.getThreadTabFunctionality(domain);
			return threadTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find help tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean isEnableUpdateFiles(String domainIdentifier){
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality updateFilesFunctionality = functionalityReadOnlyService.getUpdateFilesFunctionality(domain);
			return updateFilesFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find update files functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean isEnableCustomLogoLink(String domainIdentifier){
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality customLogoLinkFunctionality = functionalityReadOnlyService.getCustomLinkLogoFunctionality(domain);
			return customLogoLinkFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find custom logo link functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean isEnableCreateThread(String domainIdentifier){
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality createThreadFunctionality = functionalityReadOnlyService.getThreadCreationPermissionFunctionality(domain);
			return createThreadFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find thread creation functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean isEnableHelpTab(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality helpTabFunctionality = functionalityReadOnlyService.getHelpTabFunctionality(domain);
			return helpTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find help tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean isEnableListTab(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality listTabFunctionality = functionalityReadOnlyService.getListTabFunctionality(domain);
			return listTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find list tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}
	
	@Override
	public boolean isEnableGuest(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality helpTabFunctionality = functionalityReadOnlyService.getGuestFunctionality(domain);
			return helpTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find help tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean getDefaultRestrictedGuestValue(String domainIdentifier) {
		return functionalityReadOnlyService.getDefaultRestrictedGuestValue(domainIdentifier);
	}


	@Override
	public boolean isRestrictedGuestEnabled(String domainIdentifier) {
		return functionalityReadOnlyService.isRestrictedGuestAllowed(domainIdentifier);
	}
}
