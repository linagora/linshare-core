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
package org.linagora.linshare.core.facade.webservice.common.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.entities.RecipientFavourite;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.MoreObjects;
import io.swagger.v3.oas.annotations.media.Schema;


@JsonDeserialize(builder = FavouriteRecipientDto.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "FavouriteRecipient")
@Schema(name = "FavouriteRecipient", description = "Favourite Recipient")
public class FavouriteRecipientDto {

	public static Builder builder() {
		return new Builder();
	}

	public static FavouriteRecipientDto from(RecipientFavourite recipientFavourite) {
		return builder()
			.recipient(recipientFavourite.getRecipient())
			.build();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {

		protected String recipient;

		public Builder recipient(String recipient) {
			this.recipient = recipient;
			return this;
		}

		public FavouriteRecipientDto build() {
			Validate.notBlank(recipient, "'recipient' must be set.");
			return new FavouriteRecipientDto(recipient);
		}
	}

	@Schema(description = "FavouriteRecipient's recipient", required = true)
	private final String recipient;

	private FavouriteRecipientDto(String recipient) {
		this.recipient = recipient;
	}

	public String getRecipient() {
		return recipient;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("recipient", recipient)
			.toString();
	}
}
