/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.dto;

import org.linagora.linshare.core.domain.constants.UserProviderType;
import org.linagora.linshare.core.domain.entities.TwakeUserProvider;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeUserProvider", description = "A Twake user provider")
public class TwakeUserProviderDto extends AbstractUserProviderDto {

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

	@Schema(description = "Twake companyId used for retrieving users", required = true)
	private String twakeCompanyId;

	@Schema(defaultValue = "TWAKE_PROVIDER")
	@Override
	public UserProviderType getType() {
		return UserProviderType.TWAKE_PROVIDER;
	}

	protected TwakeUserProviderDto() {
		super();
	}

	public TwakeUserProviderDto(TwakeUserProvider userProvider) {
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
