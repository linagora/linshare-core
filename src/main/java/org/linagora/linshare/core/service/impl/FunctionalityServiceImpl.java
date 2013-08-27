package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
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
	public boolean activationPolicyIsMutable(Functionality f) {
		Assert.notNull(f);
		return functionalityBusinessService.activationPolicyIsMutable(f);
	}

	@Override
	public boolean configurationPolicyIsMutable(Functionality f) {
		Assert.notNull(f);
		return functionalityBusinessService.configurationPolicyIsMutable(f);
	}
}
