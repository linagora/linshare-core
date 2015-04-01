/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.domain.entities;

/**
 * An allowed contact for a restricted guest. This object represents
 * the pair (guest, allowed contact)
 * 
 * @author sduprey
 *
 */
public class AllowedContact {

	private User contact;
	private User owner;
	private Long persistenceId;

	/** constructor for hibernate **/
	protected AllowedContact() {
	};

	public AllowedContact(User owner, User contact) {
		this.contact = contact;
		this.owner = owner;
	}

	@Override
	public boolean equals(Object o) {
		if (null != o && o instanceof AllowedContact) {
			return (o == this || (((AllowedContact) o).getOwner().equals(this.getOwner()) && ((AllowedContact) o)
							.getContact().equals(this.getContact())));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getOwner().hashCode() + this.getContact().hashCode();
	}

	public void setContact(User contact) {
		this.contact = contact;
	}

	public User getContact() {
		return contact;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public User getOwner() {
		return owner;
	}

	public Long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(Long persistenceId) {
		this.persistenceId = persistenceId;
	}

}
