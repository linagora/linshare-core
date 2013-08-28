package org.linagora.linshare.core.business.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;

public interface FunctionalityBusinessService {
	
	Set<Functionality> getAllFunctionalities(AbstractDomain domain);
	
	Set<Functionality> getAllFunctionalities(String domain);
	
	boolean activationPolicyIsMutable(Functionality f, String domain);
	
	boolean configurationPolicyIsMutable(Functionality f, String domain);
	
	Functionality getFunctionality(String domainId, String functionalityId);
	
//	void update(String domain, FunctionalityDto func) throws BusinessException;
}
