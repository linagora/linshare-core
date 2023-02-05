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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.UpgradeTaskBusinessService;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.UpgradeTaskService;

public class UpgradeTaskServiceImpl extends GenericAdminServiceImpl implements UpgradeTaskService {

	protected UpgradeTaskBusinessService businessService;

	public UpgradeTaskServiceImpl(UpgradeTaskBusinessService businessService,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService) {
		super(sanitizerInputHtmlBusinessService);
		this.businessService = businessService;
	}

	@Override
	public UpgradeTask find(Account actor, UpgradeTaskType identifier) {
		preChecks(actor);
		UpgradeTask task = businessService.find(identifier);
		if (task == null) {
			throw new BusinessException(BusinessErrorCode.UPGRADE_TASK_NOT_FOUND, "Can not find upgrade task  : " + identifier );
		}
		return task;
	}

	@Override
	public List<UpgradeTask> findAll(Account actor, boolean hidden) {
		preChecks(actor);
		return businessService.findAll(hidden);
	}

	@Override
	public UpgradeTask update(Account actor, UpgradeTask upgradeTask) throws BusinessException {
		preChecks(actor);
		return businessService.update(upgradeTask);
	}

}
