package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityOldService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

public class FunctionalityFacadeImpl extends AdminGenericFacadeImpl implements FunctionalityFacade {

	private AbstractDomainService abstractDomainService;
	private FunctionalityService functionalityService;
	private FunctionalityOldService functionalityOldService;

	public FunctionalityFacadeImpl(final AccountService accountService, final AbstractDomainService abstractDomainService,
			final FunctionalityService functionalityService, FunctionalityOldService functionalityOldService) {
		super(accountService);
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.functionalityOldService = functionalityOldService;
	}

	@Override
	public FunctionalityDto get(String domain, String identifier) throws BusinessException {
		Functionality f = functionalityService.getFunctionality(domain, identifier);
		boolean parentAllowAPUpdate = functionalityService.activationPolicyIsMutable(f, domain);
		boolean parentAllowCPUpdate = functionalityService.configurationPolicyIsMutable(f, domain);
		FunctionalityDto func = new FunctionalityDto(f, parentAllowAPUpdate, parentAllowCPUpdate);
		return func;
	}

	@Override
	public List<FunctionalityDto> getAll(String domain) throws BusinessException {
		Set<Functionality> entities = functionalityService.getAllFunctionalities(domain);
		
		List<FunctionalityDto> ret = new ArrayList<FunctionalityDto>();
		for (Functionality f : entities) {
			boolean parentAllowAPUpdate = functionalityService.activationPolicyIsMutable(f, domain);
			boolean parentAllowCPUpdate = functionalityService.configurationPolicyIsMutable(f, domain);
			FunctionalityDto func = new FunctionalityDto(f, parentAllowAPUpdate, parentAllowCPUpdate);
			ret.add(func);
			this.update(domain, func);
		}
		
		return ret;
	}

	@Override
	public void update(String domain, FunctionalityDto func) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		Functionality f = functionalityService.getFunctionality(domain, func.getIdentifier());
		
		String ap = func.getActivationPolicy().getPolicy().trim().toUpperCase();
		f.getActivationPolicy().setPolicy(Policies.valueOf(ap));
		f.getActivationPolicy().setStatus(func.getActivationPolicy().getStatus());
		
		String cp = func.getConfigurationPolicy().getPolicy().trim().toUpperCase();
		f.getConfigurationPolicy().setPolicy(Policies.valueOf(cp));
		f.getConfigurationPolicy().setStatus(func.getConfigurationPolicy().getStatus());

		functionalityOldService.update(domain, f);
		
//		f.updateFunctionalityFrom(func)
		
	}
}