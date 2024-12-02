package org.linagora.linshare.core.domain.entities;

import java.util.Objects;

/**
 * <p>Represents a relationship between an Account and a ContactList in the system.</p>
 * <p>This class contains information about an account's association with a contact list,
 * including permissions related to viewing the members of the associated contact list.
 * It is used to control access to contact lists and their members for specific accounts.</p>
 *
 * <p>The class uses a composite key, represented by {@link AccountContactListId}, to uniquely identify the relationship
 * between an account and a contact list.</p>
 *
 * <p>In some cases, the permission to view contact list members can be null, in which case it will be defined by the
 * account's default setting {@link Account #defaultCanViewContactListMembers}.</p>
 *
 * <p>Methods in this class allow you to retrieve or set the contact list, account, and permission for viewing members.</p>
 */
public class AccountContactLists {

	/**
	 * The composite key that uniquely identifies the relationship between an account and a contact list.
	 * This field is used to associate a specific account with a specific contact list.
	 * The key is represented by the {@link AccountContactListId} class, which combines both the account and contact list identifiers.
	 */
	private AccountContactListId id;

	private Account account;

	private ContactList contactList;

	/**
	 * <p>Indicates whether the account has permission to view members of the associated
	 * contact list. Can be {@code null}, in such case the permission will be defined by
	 * {@link Account #defaultCanViewContactListMembers}.</p>
	 * <p>This field is typically used to control access to contact list visibility,
	 * allowing only authorized account to see member details.</p>
	 */
	private Boolean canViewContactListMembers;

	public AccountContactLists(Account account, ContactList contactList) {
		this.account = account;
		this.contactList = contactList;
	}

	public AccountContactLists(final Account account, final ContactList contactList, final boolean canViewContactListMembers) {
		this(account, contactList);
		this.canViewContactListMembers = canViewContactListMembers;
	}

	public AccountContactLists(final Account account, final ContactList contactList, final boolean canViewContactListMembers,
			final AccountContactListId id) {
		this(account, contactList, canViewContactListMembers);
		this.id = id;
	}

	public AccountContactLists() {
	}

	public AccountContactListId getId() {
		return id;
	}

	public void setId(AccountContactListId id) {
		this.id = id;
	}

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

	public Boolean isCanViewContactListMembers() {
		return canViewContactListMembers;
	}

	public void setCanViewContactListMembers(final Boolean canViewContactListMembers) {
		this.canViewContactListMembers = canViewContactListMembers;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		AccountContactLists that = (AccountContactLists) o;
		return Objects.equals(id, that.id) && Objects.equals(account, that.account) && Objects.equals(contactList,
				that.contactList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, account, contactList);
	}
}
