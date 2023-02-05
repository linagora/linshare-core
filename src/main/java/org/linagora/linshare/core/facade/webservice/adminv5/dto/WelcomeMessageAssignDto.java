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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.Validate;


@JsonDeserialize(builder = WelcomeMessageAssignDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
	name = "WelcomeMessageAssign",
	description = "Welcome message assign"
)
public class WelcomeMessageAssignDto {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private Boolean assign;

		public Builder assign(boolean assign) {
			this.assign = assign;
			return this;
		}

		public WelcomeMessageAssignDto build() {
			Validate.notNull(assign, "assign must be set.");
			return new WelcomeMessageAssignDto(assign);
		}
	}

	@Schema(description = "Assign to current domain",
			required = true)
	private final boolean assign;

	private WelcomeMessageAssignDto(boolean assign) {
		this.assign = assign;
	}

	public boolean isAssign() {
		return assign;
	}
}
