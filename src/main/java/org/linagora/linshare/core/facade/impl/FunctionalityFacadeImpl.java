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
import org.linagora.linshare.core.service.FunctionalityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityFacadeImpl implements FunctionalityFacade {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityFacadeImpl.class);
	private final FunctionalityService functionalityService;
	private final AbstractDomainService abstractDomainService;
	
	
	public FunctionalityFacadeImpl(FunctionalityService functionalityService, AbstractDomainService abstractDomainService) {
		super();
		this.functionalityService = functionalityService;
		this.abstractDomainService = abstractDomainService;
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
					logger.error("Unknown Unit Functionality Type for : " + functionality.getIdentifier());;
				}
			} else if(functionality.getType().equals(FunctionalityType.UNIT_BOOLEAN)) {
				UnitBooleanValueFunctionality f = (UnitBooleanValueFunctionality)functionality;
				
				if(f.getUnit().getUnitType().equals(UnitType.TIME)) {
					TimeUnitClass timeUnit = (TimeUnitClass)f.getUnit();
					res.add(new TimeValueBooleanFunctionalityVo(functionality.getIdentifier(), domainIdentifier, f.getValue(), timeUnit.getUnitValue(), f.isBool()));
				} else {
					logger.error("Unknown Unit boolean Functionality Type for : " + functionality.getIdentifier());;
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
			IntegerValueFunctionality completionFunctionality = functionalityService.getCompletionFunctionality(domain);
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
			Functionality userTabFunctionality = functionalityService.getUserTabFunctionality(domain);
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
			Functionality auditTabFunctionality = functionalityService.getAuditTabFunctionality(domain);
			return auditTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find audit tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean isEnableHelpTab(String domainIdentifier) {
		try {
			AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
			Functionality helpTabFunctionality = functionalityService.getHelpTabFunctionality(domain);
			return helpTabFunctionality.getActivationPolicy().getStatus();
		} catch (BusinessException e) {
			logger.error("Can't find help tab functionality for domain : " + domainIdentifier);
			logger.debug(e.getMessage());
		}
		return false;
	}
	
}
