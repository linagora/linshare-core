/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
