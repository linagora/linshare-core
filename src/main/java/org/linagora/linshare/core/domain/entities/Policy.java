/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.Policies;

/**
 * this class is designed to handle the 'enable/disable' status of a particular functionality.
 */
public class Policy implements Cloneable {
	/**
	 * Database persistence identifier
	 */
	private long persistenceId;

	/**
	 * enable/disable the policy
	 */
	private boolean status;

	private Policies policy = Policies.ALLOWED;

	private boolean defaultStatus = false;

	/**
	 * configure if the Policy is system or not, if it should be used by the ihm
	 */
	private boolean system;

	/**
	 * This member is not persisted into the database.
	 * It is only used to compute update right.
	 */
	private Boolean parentAllowUpdate;

	public Policy() {
		super();
	}
	
	public Policy(Policies policy, boolean default_status,boolean system) {
		this(policy,default_status);
		this.system = system;
	}
	public Policy(Policies policy, boolean defaultStatus) {
		super();
		this.defaultStatus = defaultStatus;
		setPolicy(policy);
		system = false;
		status = defaultStatus;
		applyConsistency();
	}
	
	public Policy(Policies policy) {
		super();
		setPolicy(policy);
	}
	
	public Policy(Policy current) {
		super();
		setDefaultStatus(current.getDefaultStatus());
		setStatus(current.getStatus());
		setPolicy(current.getPolicy());
		setSystem(current.isSystem());
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public Policies getPolicy() {
		return policy;
	}

	public void setPolicy(Policies policy) {
		this.policy = policy;
	}

	public long getId() {
		return persistenceId;
	}

	public void setId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public boolean getDefaultStatus() {
		return defaultStatus;
	}

	private void setDefaultStatus(boolean defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public Boolean getParentAllowUpdate() {
		return parentAllowUpdate;
	}

	public void setParentAllowUpdate(Boolean parentAllowUpdate) {
		this.parentAllowUpdate = parentAllowUpdate;
	}

	public String toString() {
		return "Policy=" + persistenceId + ":status=" + status + ":policy=" + policy;
	}

	/**
	 * this method returns true if a child can alter this policy 
	 * @return boolean
	 */
	public boolean isMutable() {
		if(!system) {
			if(policy.equals(Policies.ALLOWED)) {
				return true;
			}
		}
		return false;
	}

	public boolean businessEquals(Object obj) {
		Policy p = (Policy)obj;
		if(system == p.isSystem()) {
			if(status == p.getStatus()) {
				if(policy.toInt() == p.getPolicy().toInt()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Object clone() {
		Policy p = null;
		    try {
		      	p = (Policy) super.clone();
		    } catch(CloneNotSupportedException cnse) {
		      	cnse.printStackTrace(System.err);
		    }
		    p.persistenceId=0;
		    return p;
	}

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}
	
	
	public void updatePolicyFrom(Policy obj) {
		this.setStatus(obj.getStatus());
		this.setPolicy(obj.getPolicy());
	}
	
	public void applyConsistency() {
		if(policy.toInt() ==  Policies.FORBIDDEN.toInt()) {
			status=false;
		} else if(policy.toInt() ==  Policies.MANDATORY.toInt()) {
			status=true;
		}
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
		Policy other = (Policy) obj;
		if (persistenceId != other.persistenceId)
			return false;
		return true;
	}

	/**
	 * Helpers
	 */
	public boolean isForbidden() {
		return this.getPolicy().equals(Policies.FORBIDDEN);
	}

	public boolean isMandatory() {
		return this.getPolicy().equals(Policies.MANDATORY);
	}
}
