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

import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.TwakeGuestUserProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeGuestUserProvider", description = "A Twake Guest user provider")
public class TwakeGuestUserProviderDto extends AbstractUserProviderDto {

	@Schema(description = "Twake server lite dto used only as reference when creating providers", title = "TwakeServer (nested)")
	public class TwakeServerDto {

		@Schema(description = "TwakeServer's uuid", required = true)
		protected String uuid;

		@Schema(description = "TwakeServer's name", required = false)
		protected String name;

		public TwakeServerDto() {
			super();
		}

		protected TwakeServerDto(String uuid, String name) {
			super();
			this.uuid = uuid;
			this.name = name;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("uuid", uuid)
				.add("name", name)
				.toString();
		}
	}

	@Schema(required = true)
	private TwakeServerDto twakeServer;

	@Schema(description = "Twake companyId used for retrieving guest users", required = true)
	private String twakeCompanyId;

	@Schema(defaultValue = "TWAKE_GUEST_PROVIDER")
	@Override
	public UserProviderType getType() {
		return UserProviderType.TWAKE_GUEST_PROVIDER;
	}

	protected TwakeGuestUserProviderDto() {
		super();
	}

	public TwakeGuestUserProviderDto(TwakeGuestUserProvider userProvider) {
		super(userProvider);
		this.twakeServer =
			new TwakeServerDto(userProvider.getTwakeConnection().getUuid(), userProvider.getTwakeConnection().getLabel());
		this.twakeCompanyId = userProvider.getTwakeCompanyId();
		this.type = UserProviderType.TWAKE_PROVIDER;
	}

	public TwakeServerDto getTwakeServer() {
		return twakeServer;
	}

	public void setTwakeServer(TwakeServerDto twakeServer) {
		this.twakeServer = twakeServer;
	}

	public String getTwakeCompanyId() {
		return twakeCompanyId;
	}

	public void setTwakeCompanyId(String twakeCompanyId) {
		this.twakeCompanyId = twakeCompanyId;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("twakeServer", twakeServer)
			.add("twakeCompanyId", twakeCompanyId)
			.toString();
	}

}
