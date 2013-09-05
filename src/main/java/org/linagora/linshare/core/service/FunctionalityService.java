package org.linagora.linshare.core.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;

public interface FunctionalityService {
	
	Set<Functionality> getAllFunctionalities(AbstractDomain domain);
	
	Set<Functionality> getAllFunctionalities(String domain);
	
	boolean activationPolicyIsMutable(Functionality f, String domain);
	
	boolean configurationPolicyIsMutable(Functionality f, String domain);
	
	Functionality getFunctionality(String domainId, String functionalityId);
	
	void deleteFunctionality(Account actor, String domainId, String functionalityId) throws IllegalArgumentException, BusinessException;
}
