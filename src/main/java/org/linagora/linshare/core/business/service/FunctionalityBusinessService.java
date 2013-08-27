package org.linagora.linshare.core.business.service;

import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;

public interface FunctionalityBusinessService {
	
	public Set<Functionality> getAllFunctionalities(AbstractDomain domain);
	
	public Set<Functionality> getAllFunctionalities(String domain);
	
	boolean activationPolicyIsMutable(Functionality f);
	
	boolean configurationPolicyIsMutable(Functionality f);

}
