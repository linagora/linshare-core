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
