
package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.FunctionalityType;
import org.linagora.linShare.core.domain.vo.FunctionalityVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Functionality implements Cloneable {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	/**
	 * An unique string code designed to identify a Functionnality. This code should represent the Functionnality.
	 * Ex : QUOTA_USER for the user quota Functionnality.
	 */
	private String identifier;

	/**
	 * configure if the Functionnality is system or not, if it should be used by the ihm
	 */
	private boolean system;

	private Policy activationPolicy;

	private Policy configurationPolicy;
	
	private AbstractDomain domain;
	
	/**
	 * Logger available to subclasses.
	 */
	protected final Logger logger = LoggerFactory.getLogger(Functionality.class);
	
	public Functionality() {
		super();
	}
	
	public Functionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain) {
		super();
		this.identifier = identifier;
		this.system = system;
		this.activationPolicy = activationPolicy;
		this.configurationPolicy = configurationPolicy;
		this.domain = domain;
	}
	
	public FunctionalityType getType() {
		return FunctionalityType.DEFAULT;
	}

	public long getId() {
		return persistenceId;
	}

	public void setId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}

	public Policy getActivationPolicy() {
		return activationPolicy;
	}

	public void setActivationPolicy(Policy activationPolicy) {
		this.activationPolicy = activationPolicy;
	}

	public Policy getConfigurationPolicy() {
		return configurationPolicy;
	}

	public void setConfigurationPolicy(Policy configurationPolicy) {
		this.configurationPolicy = configurationPolicy;
	}

	public AbstractDomain getDomain() {
		return domain;
	}

	public void setDomain(AbstractDomain domain) {
		this.domain = domain;
	}

	@Override
	public String toString() {
		return "Functionality identifier is : " + identifier + "(" + persistenceId + ")";
	}

	public boolean businessEquals(Functionality fonc, boolean checkPolicies) {
		
		if(identifier.equals(fonc.getIdentifier())) {
			if(system == fonc.isSystem()) {
				if(checkPolicies) {
					if(configurationPolicy.businessEquals(fonc.getConfigurationPolicy())) {
						if(activationPolicy.businessEquals(fonc.getActivationPolicy())) {
							logger.debug("Functionality : " + this.toString() + " is equal to Functionality " + fonc.toString());
							return true;
						}
					}
				} else {
					logger.debug("Functionality : " + this.toString() + " is equal to Functionality " + fonc.toString());
					return true;
				}
			}
		}
		logger.debug("Functionality : " + this.toString() + " is not equal to Functionality " + fonc.toString());
		return false;
	}
	
	public Object clone() {
		// Every properties are clones, except domain.
		Functionality func = null;
		    try {
		      	func = (Functionality) super.clone();
		    } catch(CloneNotSupportedException cnse) {
		      	cnse.printStackTrace(System.err);
		    }
		    
		    func.activationPolicy = (Policy) activationPolicy.clone();
		    func.configurationPolicy = (Policy) configurationPolicy.clone();
		    func.persistenceId=0;
		    return func;
  	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (persistenceId ^ (persistenceId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Functionality other = (Functionality) obj;
		if (persistenceId != other.persistenceId)
			return false;
		return true;
	}
		
	public void updateFunctionalityFrom(Functionality functionality) {
		this.activationPolicy.updatePolicyFrom(functionality.getActivationPolicy());
		this.configurationPolicy.updatePolicyFrom(functionality.getConfigurationPolicy());
	}
	
	
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		// no data in this class.
	}
	
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {
		logger.debug("Functionality:updateFunctionalityValuesOnlyFromVo : " + identifier + " : ");
		// no data in this class.
	}
	
	
	
}
