package org.linagora.linshare.core.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;

public interface FunctionalityService {
	
	Set<Functionality> getAllFunctionalities(AbstractDomain domain);
	
	Set<Functionality> getAllFunctionalities(String domain);
	
	boolean activationPolicyIsMutable(Functionality f);
	
	boolean configurationPolicyIsMutable(Functionality f);
}
