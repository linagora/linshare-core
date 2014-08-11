package org.linagora.linshare.core.rac.impl;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.rac.EntryResourceAccessControl;

public abstract class EntryResourceAccessControlImpl<E extends Entry> extends
		AbstractResourceAccessControlImpl<Account, E> implements
		EntryResourceAccessControl<E> {

	@Override
	protected String getEntryRepresentation(E entry) {
		StringBuilder sb = new StringBuilder(entry.getEntryType().toString());
		sb.append(" (");
		sb.append(entry.getUuid());
		sb.append(") ");
		return sb.toString();
	}

	@Override
	protected Account getOwner(Entry entry) {
		Account owner = entry.getEntryOwner();
		return owner;
	}

	@Override
	protected String getOwnerRepresentation(Account owner) {
		return owner.getAccountReprentation();
	}
}
