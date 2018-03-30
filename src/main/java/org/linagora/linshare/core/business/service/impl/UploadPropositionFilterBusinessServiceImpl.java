/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.UploadPropositionFilterBusinessService;
import org.linagora.linshare.core.domain.entities.UploadPropositionAction;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilterOLD;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadPropositionActionRepository;
import org.linagora.linshare.core.repository.UploadPropositionFilterRepository;
import org.linagora.linshare.core.repository.UploadPropositionRuleRepository;
import org.linagora.linshare.mongo.entities.UploadPropositionFilter;
import org.linagora.linshare.mongo.repository.UploadPropositionFilterMongoRepository;

public class UploadPropositionFilterBusinessServiceImpl implements
		UploadPropositionFilterBusinessService {

	private final UploadPropositionFilterRepository repository;

	private final UploadPropositionRuleRepository ruleRepository;

	private final UploadPropositionActionRepository actionRepository;

	protected final UploadPropositionFilterMongoRepository uploadPropositionFilterMongoRepository;

	public UploadPropositionFilterBusinessServiceImpl(
			UploadPropositionFilterRepository repository,
			UploadPropositionRuleRepository ruleRepository,
			UploadPropositionActionRepository actionRepository,
			UploadPropositionFilterMongoRepository uploadPropositionFilterMongoRepository) {
		super();
		this.repository = repository;
		this.ruleRepository = ruleRepository;
		this.actionRepository = actionRepository;
		this.uploadPropositionFilterMongoRepository = uploadPropositionFilterMongoRepository;
	}

	@Override
	public UploadPropositionFilterOLD findOLD(String uuid) {
		return repository.find(uuid);
	}

	@Override
	public UploadPropositionFilter find(String domainUuid, String uuid) {
		return uploadPropositionFilterMongoRepository.findByDomainUuidAndUuid(domainUuid, uuid);
	}

	@Override
	public List<UploadPropositionFilter> findByDomainUuid(String domainUuid) {
		return uploadPropositionFilterMongoRepository.findByDomainUuid(domainUuid);
	}

	@Override
	public List<UploadPropositionFilter> findAll() {
		return uploadPropositionFilterMongoRepository.findAll();
	}

	@Override
	public List<UploadPropositionFilterOLD> findAllEnabledFilters() {
		return repository.findAllEnabledFilters();
	}

	@Override
	public UploadPropositionFilter create(UploadPropositionFilter uploadPropositionFilter) throws BusinessException {
		int size = findAll().size();
		uploadPropositionFilter.setOrder(size);
		return uploadPropositionFilterMongoRepository.insert(uploadPropositionFilter);
	}

	@Override
	public UploadPropositionFilter update(UploadPropositionFilter uploadPropositionFilter) throws BusinessException {
		return uploadPropositionFilterMongoRepository.save(uploadPropositionFilter);
	}

	@Override
	public void delete(UploadPropositionFilter uploadPropositionFilter) throws BusinessException {
		uploadPropositionFilterMongoRepository.delete(uploadPropositionFilter);
	}

	@Override
	public void delete(UploadPropositionRule entity) throws BusinessException {
		ruleRepository.delete(entity);
	}

	@Override
	public void delete(UploadPropositionAction entity) throws BusinessException {
		actionRepository.delete(entity);
	}

}
