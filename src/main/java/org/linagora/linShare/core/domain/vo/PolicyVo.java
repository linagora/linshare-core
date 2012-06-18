
package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.constants.Policies;
import org.linagora.linShare.core.domain.entities.Policy;

/**
 * this class is designed to handle the 'enable/disable' status of a particular functionality.
 */
public class PolicyVo implements Comparable{

	private String domainIdentifier;
	
	private String functionalityIdentifier;
	
	private String name;

	/**
	 * enable/disable the policy
	 */
	private boolean status;

	private Policies policy = Policies.ALLOWED;

	private boolean defaultStatus;

	public PolicyVo() {
		super();
	}
	
	public PolicyVo(Policy policy, String funcId, String domainIdentifier) {
		super();
		this.status = policy.getStatus();
		this.policy = policy.getPolicy();
		this.defaultStatus = policy.getDefaultStatus();
		this.functionalityIdentifier = funcId;
		this.domainIdentifier = domainIdentifier;
	}
	
	public PolicyVo(PolicyVo current) {
		super();
		setDefaultStatus(current.getDefaultStatus());
		setStatus(current.getStatus());
		setPolicy(current.getPolicy());
		this.functionalityIdentifier = current.getFunctionalityIdentifier();
		this.domainIdentifier = current.getDomainIdentifier();
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
//		System.out.println("functionalityIdentifier: " + functionalityIdentifier + " :setStatus : " + status);
		this.status = status;
	}

	public Policies getPolicy() {
		return policy;
	}

	public void setPolicy(Policies policy) {
//		System.out.println("functionalityIdentifier: " + functionalityIdentifier + " :setPolicy : " + policy);
		this.policy = policy;
	}

	public boolean getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(boolean default_status) {
		this.defaultStatus = default_status;
	}

	public String toString() {
		return "Policy=" + functionalityIdentifier + ":status=" + status + ":policy=" + policy;
	}

	public String getFunctionalityIdentifier() {
		return functionalityIdentifier;
	}

	public void setFunctionalityIdentifier(String functionalityIdentifier) {
		this.functionalityIdentifier = functionalityIdentifier;
	}

	public String getDomainIdentifier() {
		return domainIdentifier;
	}

	public void setDomainIdentifier(String domainIdentifier) {
		this.domainIdentifier = domainIdentifier;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Object activationPolicies) {
		PolicyVo obj = (PolicyVo)activationPolicies;
		return this.functionalityIdentifier.compareToIgnoreCase(obj.getFunctionalityIdentifier());
	}
}
