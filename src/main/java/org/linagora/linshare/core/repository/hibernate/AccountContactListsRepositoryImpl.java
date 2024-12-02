package org.linagora.linshare.core.repository.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountContactLists;
import org.linagora.linshare.core.domain.entities.ContactList;
import org.linagora.linshare.core.repository.AccountContactListsRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public class AccountContactListsRepositoryImpl extends AbstractRepositoryImpl<AccountContactLists>
		implements AccountContactListsRepository {
	/**
	 * Constructor.
	 *
	 * @param hibernateTemplate the hibernate template.
	 */
	public AccountContactListsRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	protected DetachedCriteria getNaturalKeyCriteria(AccountContactLists entity) {
		DetachedCriteria det = DetachedCriteria.forClass(AccountContactLists.class)
				.add(Restrictions.eq("account", entity.getAccount()))
				.add(Restrictions.eq("contactList", entity.getContactList()));
		return det;
	}

	@Override
	public List<AccountContactLists> findByAccount(@NotNull final Account account) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AccountContactLists.class)
				.add(Restrictions.eq("account", account));
		return listByCriteria(criteria);
	}

	@Override
	public @Nonnull List<AccountContactLists> findByAccountAndContactListName(@NotNull final Account account, @NotNull final String contactListNameFilter) {
		DetachedCriteria det = DetachedCriteria.forClass(getPersistentClass());
		det.createAlias("contactList", "cl");
		det.add(Restrictions.eq("account", account));
		if (!contactListNameFilter.isEmpty()) {
			det.add(Restrictions.ilike("cl.identifier", "%" + contactListNameFilter + "%"));
		}
		return findByCriteria(det);
	}

	@Override
	public @Nonnull Optional<AccountContactLists> findByAccountAndContactList(@NotNull final Account account, @NotNull final ContactList contactList) {
		if (account == null || contactList == null) {
			throw new IllegalArgumentException("Account and ContactList cannot be null.");
		}
		return findByCriteria(Restrictions.and(
				Restrictions.eq("account", account),
				Restrictions.eq("contactList", contactList)
		)).stream().findFirst();

	}

}


