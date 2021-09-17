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

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Policy")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyDto {

	public class Config {

		@Schema(description = "The current value")
		protected boolean value;

		@Schema(description = "The current value of my domain ancestor (parent domain)")
		protected boolean parentValue;

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

		@Override
		public String toString() {
			return "Config [value=" + value + ", parentValue=" + parentValue + "]";
		}

	}

	@Schema(description = "Indicate if this policy should be displayed or hidden")
	protected boolean hidden;

	@Schema(description = "Indicate if this policy should be displayed in read only mode")
	protected boolean readonly;

	@Schema(name = "Enable", description = "Describe is this policy is enable or not")
	protected Config enable;

	@Schema(name = "AllowOverride", description = "Describe if this policy can be overriden")
	protected Config allowOverride;

	public PolicyDto() {
		super();
	}

	public PolicyDto(Policy policy) {
		super();
		if (policy.isSystem()) {
			this.hidden = true;
			this.readonly = true;
		} else {
			this.hidden = false;
			this.readonly = !policy.getParentAllowUpdate();
		}
		// FIXME: parentValue: should be ancestor value, default value should be used when there is no ancestor
		boolean parentValue = policy.getDefaultStatus();
		this.enable = new Config(policy.getStatus(), parentValue);
		boolean allowOverrideValue = policy.getPolicy().equals(Policies.ALLOWED);
		this.allowOverride = new Config(allowOverrideValue, policy.getParentAllowUpdate());
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
		return "PolicyDto [hidden=" + hidden + ", readonly=" + readonly + ", enable=" + enable + ", allowOverride="
				+ allowOverride + "]";
	}

}
