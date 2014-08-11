package org.linagora.linshare.core.rac;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Entry;

public interface EntryResourceAccessControl<E extends Entry> extends
		AbstractResourceAccessControl<Account, E> {

}
