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
package org.linagora.linshare.core.domain.entities;

import java.util.Date;
import java.util.UUID;

import com.google.common.base.MoreObjects;

public class GDPRExternalRecipientFavourite {

	private Long id;
	private String uuid;
	private long recipientFavouritePersistenceId;
	private Date expirationDate;

	protected GDPRExternalRecipientFavourite(){};

	public GDPRExternalRecipientFavourite(RecipientFavourite recipientFavourite) {
		this.uuid = UUID.randomUUID().toString();
		this.recipientFavouritePersistenceId = recipientFavourite.getPersistenceId();
		this.expirationDate = recipientFavourite.getExpirationDate();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long persistenceId) {
		this.id = persistenceId;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getRecipientFavouritePersistenceId() {
		return recipientFavouritePersistenceId;
	}

	public void setRecipientFavouritePersistenceId(long recipientFavouritePersistenceId) {
		this.recipientFavouritePersistenceId = recipientFavouritePersistenceId;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("id", id)
			.add("uuid", uuid)
			.add("recipientFavouritePersistenceId",  recipientFavouritePersistenceId)
			.add("expirationDate", expirationDate)
			.toString();
	}
}
