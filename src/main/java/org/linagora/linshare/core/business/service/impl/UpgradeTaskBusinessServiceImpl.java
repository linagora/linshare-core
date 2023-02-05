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
package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.UpgradeTaskBusinessService;
import org.linagora.linshare.core.domain.constants.UpgradeTaskType;
import org.linagora.linshare.core.domain.entities.UpgradeTask;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UpgradeTaskRepository;

public class UpgradeTaskBusinessServiceImpl implements UpgradeTaskBusinessService {

	protected UpgradeTaskRepository repository;

	public UpgradeTaskBusinessServiceImpl(UpgradeTaskRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public UpgradeTask find(UpgradeTaskType identifier) {
		return repository.find(identifier);
	}

	@Override
	public List<UpgradeTask> findAll(boolean hidden) {
		return repository.findAllHidden(hidden);
	}

	@Override
	public UpgradeTask update(UpgradeTask upgradeTask) throws BusinessException {
		return repository.update(upgradeTask);
	}
}
