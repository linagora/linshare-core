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

import org.linagora.linshare.core.business.service.ShareEntryGroupBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ShareEntryGroup;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ShareEntryGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ShareEntryGroupBusinessServiceImpl
		implements ShareEntryGroupBusinessService {

	private final ShareEntryGroupRepository repository;

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(ShareEntryGroupBusinessServiceImpl.class);

	public ShareEntryGroupBusinessServiceImpl(
			ShareEntryGroupRepository shareEntryGroupRepository) {
		super();
		this.repository = shareEntryGroupRepository;
	}

	@Override
	public ShareEntryGroup create(ShareEntryGroup entity)
			throws BusinessException {
		return repository.create(entity);
	}

	@Override
	public void delete(ShareEntryGroup shareEntryGroup)
			throws BusinessException {
		repository.delete(shareEntryGroup);
	}

	@Override
	public ShareEntryGroup findByUuid(String uuid) throws BusinessException {
		return repository.findByUuid(uuid);
	}

	@Override
	public ShareEntryGroup update(ShareEntryGroup shareEntryGroup, ShareEntryGroup shareEntryGroupObject)
			throws BusinessException {
		shareEntryGroup.setBusinessExpirationDate(shareEntryGroupObject.getExpirationDate());
		shareEntryGroup.setBusinessNotificationDate(shareEntryGroupObject.getNotificationDate());
		shareEntryGroup.setBusinessSubject(shareEntryGroupObject.getSubject());
		return repository.update(shareEntryGroup);
	}

	@Override
	public ShareEntryGroup update(ShareEntryGroup shareEntryGroup) throws BusinessException {
		return repository.update(shareEntryGroup);
	}

	@Override
	public List<String> findAllAboutToBeNotified() {
		List<String> all= Lists.newArrayList();
		all.addAll(repository.findAllAboutToBeNotified());
		return all;
	}

	@Override
	public List<String> findAllToPurge() {
		return repository.findAllToPurge();
	}

	@Override
	public List<ShareEntryGroup> findAll(Account owner) throws BusinessException {
		return repository.findAll(owner);
	}

}
