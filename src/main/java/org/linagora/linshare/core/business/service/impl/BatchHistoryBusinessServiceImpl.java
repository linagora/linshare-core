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

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.BatchHistoryBusinessService;
import org.linagora.linshare.core.domain.constants.BatchType;
import org.linagora.linshare.core.domain.entities.BatchHistory;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.BatchHistoryRepository;

public class BatchHistoryBusinessServiceImpl implements BatchHistoryBusinessService {

	private final BatchHistoryRepository batchHistoryRepository;

	public BatchHistoryBusinessServiceImpl(final BatchHistoryRepository batchHistoryRepository) {
		super();
		this.batchHistoryRepository = batchHistoryRepository;
	}

	@Override
	public BatchHistory create(BatchHistory entity) throws BusinessException {
		entity = batchHistoryRepository.create(entity);
		return entity;
	}

	@Override
	public BatchHistory update(BatchHistory entity) throws BusinessException {
		entity = batchHistoryRepository.update(entity);
		return entity;
	}

	@Override
	public List<BatchHistory> find(Date beginDate, Date endDate, BatchType batchType, String status) {
		return batchHistoryRepository.find(beginDate, endDate, batchType, status);
	}

	@Override
	public boolean exist(Date beginDate, BatchType batchType) {
		return batchHistoryRepository.exist(beginDate, batchType);
	}

	@Override
	public BatchHistory findByBatchType(Date beginDate, Date endDate, BatchType batchType) throws BusinessException {
		return batchHistoryRepository.findByBatchType(beginDate, endDate, batchType);
	}

	@Override
	public BatchHistory findByUuid(String lsUuid) throws BusinessException {
		return batchHistoryRepository.findByUuid(lsUuid);
	}
}
