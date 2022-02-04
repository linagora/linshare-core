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

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.ServerType;
import org.linagora.linshare.core.domain.entities.TwakeConnection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonDeserialize(builder = TwakeServerDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TwakeServer", description = "A Twake server connection")
public class TwakeServerDto extends AbstractServerDto {

	public static TwakeServerDto from(TwakeConnection twakeConnection) {
		return builder()
			.clientId(twakeConnection.getClientId())
			.clientSecret(twakeConnection.getClientSecret())
			.uuid(twakeConnection.getUuid())
			.name(twakeConnection.getLabel())
			.url(twakeConnection.getProviderUrl())
			.serverType(twakeConnection.getServerType())
			.creationDate(twakeConnection.getCreationDate())
			.modificationDate(twakeConnection.getModificationDate())
			.build();
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder extends AbstractServerDtoBuilder<TwakeServerDto> {
		private String clientId;
		private String clientSecret;

		public Builder clientId(String clientId) {
			this.clientId = clientId;
			return this;
		}

		public Builder clientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
			return this;
		}

		@Override
		public TwakeServerDto build() {
			validation();
			Validate.notBlank(clientId, "clientId must be set.");
			Validate.notBlank(clientSecret, "clientSecret must be set.");
			return new TwakeServerDto(clientId, clientSecret, uuid, name, description, url, serverType, creationDate, modificationDate);
		}
	}

	@Schema(description = "clientId used to connect to Twake Console", required = false)
	private final String clientId;

	@Schema(description = "Secret used for clientId", required = false)
	private final String clientSecret;

	private TwakeServerDto(String clientId, String clientSecret, String uuid, String name, String description, String url, ServerType serverType, Date creationDate, Date modificationDate) {
		super(uuid, name, description, url, serverType, creationDate, modificationDate);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}


	public String getClientId() {
		return clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public TwakeConnection toTwakeServerObject(Optional<String> uuid) {
		TwakeConnection connection = new TwakeConnection();
		connection.setUuid(uuid.orElse(getUuid()));
		connection.setLabel(getName());
		connection.setProviderUrl(getUrl());
		connection.setClientId(getClientId());
		connection.setClientSecret(getClientSecret());
		connection.setCreationDate(getCreationDate());
		connection.setModificationDate(getModificationDate());
		return connection;
	}
}
