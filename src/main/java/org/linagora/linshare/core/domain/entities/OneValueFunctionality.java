
package org.linagora.linshare.core.domain.entities;

public abstract class OneValueFunctionality<U> extends Functionality {
	
	protected U value;
	
	public OneValueFunctionality() {
		super();
	}

	public OneValueFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain,U value) {
		super(identifier, system, activationPolicy, configurationPolicy, domain);
		this.value = value;
	}

	public U getValue() {
		return value;
	}

	public void setValue(U value) {
		this.value = value;
	}
}
