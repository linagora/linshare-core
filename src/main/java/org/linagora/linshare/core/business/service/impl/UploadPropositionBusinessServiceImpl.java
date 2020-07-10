/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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

import org.linagora.linshare.core.business.service.UploadPropositionBusinessService;
import org.linagora.linshare.core.domain.constants.UploadPropositionStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.mongo.entities.UploadProposition;
import org.linagora.linshare.mongo.repository.UploadPropositionMongoRepository;

public class UploadPropositionBusinessServiceImpl implements
		UploadPropositionBusinessService {

	private final UploadPropositionMongoRepository uploadPropositionMongoRepository;

	public UploadPropositionBusinessServiceImpl(
			final UploadPropositionMongoRepository uploadPropositionMongoRepository) {
		super();
		this.uploadPropositionMongoRepository = uploadPropositionMongoRepository;
	}

	@Override
	public List<UploadProposition> findAll() {
		return uploadPropositionMongoRepository.findAll();
	}

	@Override
	public List<UploadProposition> findAllByAccountUuid(String accountUuid) {
		return uploadPropositionMongoRepository.findByAccountUuid(accountUuid, null);
	}

	@Override
	public UploadProposition findByUuid(String uuid) {
		return uploadPropositionMongoRepository.findByUuid(uuid);
	}

	@Override
	public List<UploadProposition> findByDomainUuid(String domainUuuid) {
		return uploadPropositionMongoRepository.findByDomainUuid(domainUuuid, null);
	}

	@Override
	public UploadProposition create(UploadProposition uploadProposition) throws BusinessException {
		return uploadPropositionMongoRepository.insert(uploadProposition);
	}

	@Override
	public UploadProposition update(UploadProposition proposition)
			throws BusinessException {
		return uploadPropositionMongoRepository.save(proposition);
	}

	@Override
	public UploadProposition updateStatus(UploadProposition proposition, UploadPropositionStatus newStatus)
			throws BusinessException {
		proposition.updateStatus(newStatus);
		return update(proposition);
	}

	@Override
	public void delete(UploadProposition proposition) throws BusinessException {
		uploadPropositionMongoRepository.delete(proposition);
	}
}
