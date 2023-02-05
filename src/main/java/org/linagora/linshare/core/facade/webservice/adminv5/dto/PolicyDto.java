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
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.Policy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@Schema(name = "Policy")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyDto {

	public class Config {

		@Schema(description = "The current value")
		protected boolean value;

		@Schema(description = "The current value of my domain ancestor (parent domain)",
				accessMode = AccessMode.READ_ONLY)
		protected boolean parentValue;

		public Config() {
			super();
		}

		public Config(boolean value, boolean parentValue) {
			super();
			this.value = value;
			this.parentValue = parentValue;
		}

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public boolean isParentValue() {
			return parentValue;
		}

		public void setParentValue(boolean parentValue) {
			this.parentValue = parentValue;
		}

		@Schema(description = "Tell if parent value was overriden.", accessMode = AccessMode.READ_ONLY)
		@JsonProperty
		public boolean isOverriden() {
			return !parentValue == value;
		}

		@JsonIgnore
		public void setOverriden(boolean noop) {
		}

		@Override
		public String toString() {
			return "Config [value=" + value + ", parentValue=" + parentValue + "]";
		}

	}

	@Schema(description = "Indicate if this policy should be displayed or hidden",
			accessMode = AccessMode.READ_ONLY)
	protected boolean hidden;

	@Schema(description = "Indicate if this policy should be displayed in read only mode",
			accessMode = AccessMode.READ_ONLY)
	protected boolean readonly;

	@Schema(name = "enable", description = "Describe is this policy is enable or not")
	protected Config enable;

	@Schema(name = "allowOverride", description = "Describe if this policy can be overriden")
	protected Config allowOverride;

	public PolicyDto() {
		super();
	}

	public PolicyDto(Policy policy, Policy ancestor) {
		super();
		if (policy.isSystem()) {
			this.hidden = true;
			this.readonly = true;
		} else {
			this.hidden = false;
			this.readonly = !policy.getParentAllowUpdate();
		}
		boolean parentValue = policy.getDefaultStatus();
		if (ancestor != null) {
			parentValue = ancestor.getStatus();
		}
		this.enable = new Config(policy.getStatus(), parentValue);
		boolean allowOverrideValue = policy.getPolicy().equals(Policies.ALLOWED);
		this.allowOverride = new Config(allowOverrideValue, policy.getParentAllowUpdate());
	}

	public PolicyDto(Policy policy) {
		this(policy, null);
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Config getEnable() {
		return enable;
	}

	public void setEnable(Config enable) {
		this.enable = enable;
	}

	public Config getAllowOverride() {
		return allowOverride;
	}

	public void setAllowOverride(Config allowOverride) {
		this.allowOverride = allowOverride;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("hidden", hidden)
				.add("readonly", readonly)
				.add("enable", enable)
				.add("allowOverride", allowOverride)
				.toString();
	}

}
