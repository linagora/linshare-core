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

/**
 * The recipient favourite.
 * @author ngapaillard
 *
 */
public class RecipientFavourite extends AbstractFavourite<User>{

	/** the recipient concerned by the favorite */
	private String recipient;

	private Long persistenceId;

	protected Date expirationDate;
	
	/** constructor for hibernate **/
	protected RecipientFavourite(){};
	
	/**
	 * The constructor with definition of a recipient.
	 * @param owner
	 * @param recipient
	 */
	public RecipientFavourite(User owner, String recipient, Date expirationDate){
		super();
		super.setOwner(owner);
		super.setWeight(Long.valueOf(1));
		this.recipient=recipient;
		this.expirationDate = expirationDate;
	}

	/**
	 * Give the recipient.
	 * @return recipient the recipient.
	 */
	public String getRecipient() {
		return recipient;
	}

	/**
	 * Set the recipient.
	 * @param recipient the recipient to set.
	 */
	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Override
	public boolean equals(Object o){
		if(null != o && o instanceof RecipientFavourite) {
			return (o == this || (((RecipientFavourite)o).getOwner().equals(this.getOwner())
					&& ((RecipientFavourite)o).getRecipient().equals(this.getRecipient())));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(){
		return this.getOwner().hashCode()+this.getRecipient().hashCode();
	}

	@Override
	public String toString() {
		return "RecipientFavourite [recipient=" + recipient + ", getWeight()="
				+ getWeight() + "]";
	}

}
