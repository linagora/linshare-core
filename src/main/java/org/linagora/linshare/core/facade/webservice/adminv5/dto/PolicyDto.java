/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
