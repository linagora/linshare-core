package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link AccountContactLists} entities. This interface provides methods for
 * retrieving, purging, and deleting contact lists associated with specific account. It extends the
 * {@link AbstractRepository} to inherit common CRUD operations for {@link AccountContactLists} entities.
 */
public interface AccountContactListsRepository extends AbstractRepository<AccountContactLists> {

	/**
	 * Retrieves all {@link AccountContactLists} entities associated with a given {@link Account}. This method is useful
	 * for fetching the contact lists that an account had.
	 *
	 * @param account The {@link Account} whose allowed contact lists are to be retrieved. Must not be {@code null}.
	 * @return A {@link List} of {@link AccountContactLists} associated with the given account.
	 */
	public @Nonnull List<AccountContactLists> findByAccount(@NotNull final Account account);

	/**
	 * Finds all {@link AccountContactLists} associated with the given account and optional contact list name filter.
	 *
	 * @param account               the guest whose AccountContactLists to find
	 * @param contactListNameFilter a contact list name filter that must be included in the names of contact lists to
	 *                              return. Cannot be {@code null}. If empty, no filtering is applied. If blank, filtering is applied.
	 * @return a list of AccountContactLists that belong to the given account and match the contact list name filter.
	 */
	public @Nonnull List<AccountContactLists> findByAccountAndContactListName(@NotNull final Account account,
			@NotNull final String contactListNameFilter);

	/**
	 * Finds the AccountContactLists entry for the given account and contact list.
	 *
	 * @param account     the account to search for, must not be {@code null}.
	 * @param contactList the contact list to search for, must not be {@code null}.
	 * @return an {@link Optional} {@link AccountContactLists} matching the given criteria. Not {@code null}.
	 */
	public @Nonnull Optional<AccountContactLists> findByAccountAndContactList(@NotNull final Account account,
			@NotNull final ContactList contactList);

}
