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
package org.linagora.linshare.core.upgrade.v4_1;

import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.MimePolicyRepository;
import org.linagora.linshare.core.repository.MimeTypeRepository;
import org.linagora.linshare.mongo.repository.UpgradeTaskLogMongoRepository;

public class UpgradeApacheTikaAddAllNewMimeTypeUpgradeTaskImpl extends org.linagora.linshare.core.upgrade.v4_0.UpgradeApacheTikaAddAllNewMimeTypeUpgradeTaskImpl{

	public UpgradeApacheTikaAddAllNewMimeTypeUpgradeTaskImpl(AccountRepository<Account> accountRepository,
			UpgradeTaskLogMongoRepository upgradeTaskLogMongoRepository,
			MimePolicyRepository mimePolicyRepository,
			MimeTypeMagicNumberDao mimeTypeMagicNumberDao,
			MimeTypeRepository mimeTypeRepository) {
		super(accountRepository, upgradeTaskLogMongoRepository, mimePolicyRepository, mimeTypeMagicNumberDao,
				mimeTypeRepository);
	}

	@Override
	public UpgradeTaskType getUpgradeTaskType() {
		return UpgradeTaskType.UPGRADE_4_1_ADD_ALL_NEW_MIME_TYPE;
	}

}
