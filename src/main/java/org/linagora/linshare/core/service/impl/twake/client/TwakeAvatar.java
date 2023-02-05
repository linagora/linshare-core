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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = TwakeAvatar.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeAvatar", description = "A Twake avatar")
public class TwakeAvatar {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private String type;
		private String value;

		public Builder type(String type) {
			this.type = type;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public TwakeAvatar build() {
			return new TwakeAvatar(type, value);
		}
	}

	private final String type;
	private final String value;

	private TwakeAvatar(String type, String value) {
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("type", type)
			.add("value", value)
			.toString();
	}
}
