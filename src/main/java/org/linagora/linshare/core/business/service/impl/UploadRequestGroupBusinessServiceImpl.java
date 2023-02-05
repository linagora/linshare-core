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

import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadRequestGroupRepository;

public class UploadRequestGroupBusinessServiceImpl implements
		UploadRequestGroupBusinessService {

	private final UploadRequestGroupRepository uploadRequestGroupRepository;

	public UploadRequestGroupBusinessServiceImpl(
			final UploadRequestGroupRepository uploadRequestGroupRepository) {
		super();
		this.uploadRequestGroupRepository = uploadRequestGroupRepository;
	}

	@Override
	public List<UploadRequestGroup> findAll(Account owner, List<UploadRequestStatus> uploadRequestStatus) {
		return uploadRequestGroupRepository.findAllByOwner(owner, uploadRequestStatus);
	}

	@Override
	public UploadRequestGroup findByUuid(String uuid) {
		return uploadRequestGroupRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequestGroup create(UploadRequestGroup group)
			throws BusinessException {
		return uploadRequestGroupRepository.create(group);
	}

	@Override
	public UploadRequestGroup update(UploadRequestGroup group)
			throws BusinessException {
		return uploadRequestGroupRepository.update(group);
	}

	@Override
	public void delete(UploadRequestGroup group) throws BusinessException {
		uploadRequestGroupRepository.delete(group);
	}

	@Override
	public UploadRequestGroup updateStatus(UploadRequestGroup uploadRequestGroup, UploadRequestStatus status) throws BusinessException {
		uploadRequestGroup.updateStatus(status);
		uploadRequestGroup = uploadRequestGroupRepository.update(uploadRequestGroup);
		return uploadRequestGroup;
	}

	@Override
	public List<String> findOutdatedRequests() {
		return uploadRequestGroupRepository.findOutDateRequests();
	}

	@Override
	public Integer countNbrUploadedFiles(UploadRequestGroup uploadRequestGroup) {
		return uploadRequestGroupRepository.countNbrUploadedFiles(uploadRequestGroup);
	}

	@Override
	public Long computeEntriesSize(UploadRequestGroup uploadRequestGroup) {
		return uploadRequestGroupRepository.computeEntriesSize(uploadRequestGroup);
	}
}
