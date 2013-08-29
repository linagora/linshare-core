package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.FunctionalityService;
import org.springframework.util.Assert;

public class FunctionalityServiceImpl implements FunctionalityService {

	private FunctionalityBusinessService functionalityBusinessService;

	public FunctionalityServiceImpl(FunctionalityBusinessService functionalityBusinessService) {
		super();
		this.functionalityBusinessService = functionalityBusinessService;
	}

	@Override
	public Set<Functionality> getAllFunctionalities(AbstractDomain domain) {
		return functionalityBusinessService.getAllFunctionalities(domain);
	}

	@Override
	public Set<Functionality> getAllFunctionalities(String domain) {
		return functionalityBusinessService.getAllFunctionalities(domain);
	}

	@Override
	public boolean activationPolicyIsMutable(Functionality f, String domain) {
		Assert.notNull(f);
		Assert.notNull(domain);
		return functionalityBusinessService.activationPolicyIsMutable(f, domain);
	}

	@Override
	public boolean configurationPolicyIsMutable(Functionality f, String domain) {
		Assert.notNull(f);
		Assert.notNull(domain);
		return functionalityBusinessService.configurationPolicyIsMutable(f, domain);
	}

	@Override
	public Functionality getFunctionality(String domainId, String functionalityId) {
		Assert.notNull(domainId);
		Assert.notNull(functionalityId);
		return functionalityBusinessService.getFunctionality(domainId, functionalityId);
	}

	@Override
	public void deleteFunctionality(String domainId, String functionalityId) throws IllegalArgumentException, BusinessException {
		Assert.notNull(domainId);
		Assert.notNull(functionalityId);
		functionalityBusinessService.delete(domainId, functionalityId);
	}
}
