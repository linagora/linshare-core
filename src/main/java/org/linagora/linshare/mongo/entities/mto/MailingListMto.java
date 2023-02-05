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
package org.linagora.linshare.mongo.entities.mto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.domain.entities.ContactListContact;

import com.google.common.collect.Lists;

@XmlRootElement(name = "MailingList")
public class MailingListMto {

	protected String uuid;

	protected String name;

	protected String description;

	protected AccountMto owner;

	protected DomainMto domain;

	protected List<MailingListContactMto> contacts;

	protected boolean isPublic;

	public MailingListMto() {
	}

	public MailingListMto(ContactList list) {
		this.uuid = list.getUuid();
		this.description = list.getDescription();
		this.name = list.getIdentifier();
		this.owner = new AccountMto(list.getOwner());
		this.domain = new DomainMto(list.getDomain());
		this.isPublic = list.isPublic();
		this.contacts = Lists.newArrayList();
		for (ContactListContact m : list.getContactListContacts()) {
			contacts.add(new MailingListContactMto(m));
		}
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AccountMto getOwner() {
		return owner;
	}

	public void setOwner(AccountMto owner) {
		this.owner = owner;
	}

	public DomainMto getDomain() {
		return domain;
	}

	public void setDomain(DomainMto domain) {
		this.domain = domain;
	}

	public List<MailingListContactMto> getContacts() {
		return contacts;
	}

	public void setContacts(List<MailingListContactMto> contacts) {
		this.contacts = contacts;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

}
