package org.linagora.linshare.core.domain.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFunctionality implements Cloneable {

	/**
	 * Database persistence identifier
	 */
	protected long persistenceId;

	/**
	 * An unique string code designed to identify a Functionality. This code should represent the Functionality.
	 * Ex : QUOTA_USER for the user quota Functionality.
	 */
	protected String identifier;

	/**
	 * configure if the Functionality is system or not, if it should be used by the UI
	 */
	protected boolean system;

	protected Policy activationPolicy;

	protected Policy configurationPolicy;

	protected Policy delegationPolicy;

	protected AbstractDomain domain;
	
	/**
	 * Logger available to subclasses.
	 */
	protected final Logger logger = LoggerFactory.getLogger(Functionality.class);

	public AbstractFunctionality() {
		super();
	}

	public AbstractFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain) {
		super();
		this.identifier = identifier;
		this.system = system;
		this.activationPolicy = activationPolicy;
		this.configurationPolicy = configurationPolicy;
		this.domain = domain;
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
	
	public boolean businessEquals(AbstractFunctionality fonc, boolean checkPolicies) {
		
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
		AbstractFunctionality func = null;
		try {
			func = (AbstractFunctionality) super.clone();
		} catch (CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		func.activationPolicy = (Policy) activationPolicy.clone();
		func.configurationPolicy = (Policy) configurationPolicy.clone();
		func.persistenceId = 0;
		return func;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
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
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		return true;
	}

	public boolean equalsIdentifier(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractFunctionality other = (AbstractFunctionality) obj;
		if (!this.identifier.equals(other.identifier))
			return false;
		return true;
	}
	
	public void updateFunctionalityFrom(AbstractFunctionality functionality) {
		this.activationPolicy.updatePolicyFrom(functionality.getActivationPolicy());
		this.configurationPolicy.updatePolicyFrom(functionality.getConfigurationPolicy());
	}
	
	public void updateFunctionalityValuesOnlyFrom(AbstractFunctionality functionality) {
		// no data in this class.
	}

}
