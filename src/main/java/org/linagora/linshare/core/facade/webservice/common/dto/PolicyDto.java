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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.Policy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties({"previousPolicy"})
@XmlRootElement(name = "Policy")
@Schema(name = "Policy", description = "Theses are policies of activation or configuration for functionalities.")
public class PolicyDto {

	@Schema(description = "Policy")
	protected String policy;

	// Current status of the policy
	@Schema(description = "Status")
	protected boolean status;

	// Default value for the field status when the policy is reset (ex changing
	// policy from FORBIDEN to ALLOWED.
	@Schema(description = "DefaultStatus")
	protected boolean defaultStatus;

	// This field is designed to indicate if the parent functionality allow you
	// to update the policy.
	@Schema(description = "ParentAllowUpdate")
	protected boolean parentAllowUpdate;

	// HOOK : To be removed. For debug.
	protected Boolean system;

	public PolicyDto() {
		super();
	}

	public PolicyDto(Policy policy) {
		super();
		this.policy = policy.getPolicy().toString();
		this.status = policy.getStatus();
		this.defaultStatus = policy.getDefaultStatus();
		this.parentAllowUpdate = policy.getParentAllowUpdate();
		system = policy.isSystem();
	}

	@Override
	public String toString() {
		return "PolicyDto [policy=" + policy + ", status=" + status
				+ ", parentAllowUpdate=" + parentAllowUpdate + ", system="
				+ system + "]";
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean getDefaultStatus() {
		return defaultStatus;
	}

	public void setDefaultStatus(boolean defaultStatus) {
		this.defaultStatus = defaultStatus;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public boolean getParentAllowUpdate() {
		return parentAllowUpdate;
	}

	public void setParentAllowUpdate(boolean parentAllowUpdate) {
		this.parentAllowUpdate = parentAllowUpdate;
	}

	public Boolean isSystem() {
		return system;
	}

	public void setSystem(Boolean system) {
		this.system = system;
	}
}
