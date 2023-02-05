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
package org.linagora.linshare.core.batches.impl;

import org.linagora.linshare.core.batches.GenericUpgradeTask;
import org.linagora.linshare.core.batches.utils.impl.UpgradeTaskConsoleImpl;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public abstract class GenericUpgradeTaskImpl extends GenericBatchImpl implements GenericUpgradeTask {

	public GenericUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository) {
		super(accountRepository);
		this.console = new UpgradeTaskConsoleImpl(this.getClass(), upgradeTaskLogMongoRepository);
	}
}
