package org.linagora.linshare.core.domain.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>Represents the composite primary key linking an account to a contact list.</p>
 * <p>Contains {@code account} and {@code contactList} as key fields, with
 * proper overrides for {@link #equals(Object)} and {@link #hashCode()}.</p>
 * <p>Note: this class implements {@link Serializable} because JPA requires that for all composit key.</p>
 */
public class AccountContactListId implements Serializable {

	private static final long serialVersionUID = 7464405325831388223L;
	private Account account;
	private ContactList contactList;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public ContactList getContactList() {
		return contactList;
	}

	public void setContactList(ContactList contactList) {
		this.contactList = contactList;
	}

	public AccountContactListId() {
	}

	public AccountContactListId(Account account, ContactList contactList) {
		this.account = account;
		this.contactList = contactList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AccountContactListId that = (AccountContactListId) o;
		return Objects.equals(account, that.account) && Objects.equals(contactList, that.contactList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, contactList);
	}
}
