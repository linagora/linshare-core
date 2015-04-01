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

package org.linagora.linshare.core.domain.vo;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.entities.MailingList;
import org.linagora.linshare.core.domain.entities.MailingListContact;

public class MailingListVo {

	private String identifier;
	private String description;
	private boolean isPublic;
	private UserVo owner;
	private List<MailingListContactVo> contacts = new ArrayList<MailingListContactVo>();
	private String uuid;
	private String domainId;

	public MailingListVo() {
	}

	public MailingListVo(String identifier, String description, boolean isPublic) {
		super();
		this.identifier = identifier;
		this.description = description;
		this.isPublic = isPublic;
	}

	public MailingListVo(MailingList list) {
		this.uuid = list.getUuid();
		this.identifier = list.getIdentifier();
		this.description = list.getDescription();
		this.isPublic = list.isPublic();
		this.owner = new UserVo(list.getOwner());
		this.domainId = list.getDomain().getIdentifier();

		for (MailingListContact current : list.getMailingListContact()) {
			contacts.add(new MailingListContactVo(current));
		}
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public UserVo getOwner() {
		return owner;
	}

	public void setOwner(UserVo owner) {
		this.owner = owner;
	}

	public String getDomainId() {
		return domainId;
	}

	public void setDomainId(String domain) {
		this.domainId = domain;
	}

	public List<MailingListContactVo> getContacts() {
		return contacts;
	}

	public void addContact(MailingListContactVo contact) {
		contacts.add(contact);
	}

	public void setContacts(List<MailingListContactVo> mails) {
		this.contacts = mails;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRepresentation() {
		return identifier + " (" + owner.getCompleteName() + ")";
	}

	@Override
	public String toString() {
		return getRepresentation();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailingListVo other = (MailingListVo) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	
	/**
	 * Helpers
	 */

	/**
	 * Check if user is in mailing list
	 * 
	 * @param contacts
	 * @param mail
	 * @return
	 */
	public boolean isAlreadyAContact(MailingListContactVo contact) {
		return isAlreadyAContact(contact.getMail());
	}

	/**
	 * Check if user is in mailing list
	 * 
	 * @param contacts
	 * @param mail
	 * @return
	 */
	public boolean isAlreadyAContact(String mail) {
		for (MailingListContactVo contact : contacts) {
			if (contact.getMail().equals(mail)) {
				return true;
			}
		}
		return false;
	}
}
