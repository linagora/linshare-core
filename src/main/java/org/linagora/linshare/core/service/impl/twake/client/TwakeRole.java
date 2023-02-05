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
package org.linagora.linshare.core.service.impl.twake.client;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = TwakeRole.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeRole", description = "A Twake role")
public class TwakeRole {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private String targetCode;
		private String roleCode;
		private String status;

		public Builder targetCode(String targetCode) {
			this.targetCode = targetCode;
			return this;
		}

		public Builder roleCode(String roleCode) {
			this.roleCode = roleCode;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public TwakeRole build() {
			Validate.notBlank(targetCode, "targetCode must be set.");
			Validate.notBlank(roleCode, "roleCode must be set.");
			Validate.notBlank(status, "status must be set.");
			return new TwakeRole(targetCode, roleCode, status);
		}
	}

	private final String targetCode;
	private final String roleCode;
	private final String status;

	private TwakeRole(String targetCode, String roleCode, String status) {
		this.targetCode = targetCode;
		this.roleCode = roleCode;
		this.status = status;
	}

	public String getTargetCode() {
		return targetCode;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("targetCode", targetCode)
			.add("roleCode", roleCode)
			.add("status", status)
			.toString();
	}
}
