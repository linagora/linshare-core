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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = TwakeUsersResponse.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeUsersResponse", description = "The response of users endpoint")
public class TwakeUsersResponse {

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private int total;
		private List<TwakeUser> list;

		public Builder total(int total) {
			this.total = total;
			return this;
		}

		public Builder list(List<TwakeUser> list) {
			this.list = list;
			return this;
		}

		public TwakeUsersResponse build() {
			return new TwakeUsersResponse(total, list);
		}
	}

	private final int total;
	private final List<TwakeUser> list;

	private TwakeUsersResponse(int total, List<TwakeUser> list) {
		this.total = total;
		this.list = list;
	}

	public int getTotal() {
		return total;
	}

	public List<TwakeUser> getList() {
		return list;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("total", total)
			.add("list", list)
			.toString();
	}
}
