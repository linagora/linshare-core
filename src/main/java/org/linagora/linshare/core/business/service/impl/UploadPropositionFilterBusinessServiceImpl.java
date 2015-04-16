/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.domain.entities.UploadPropositionRule;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadPropositionActionRepository;
import org.linagora.linshare.core.repository.UploadPropositionFilterRepository;
import org.linagora.linshare.core.repository.UploadPropositionRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionFilterBusinessServiceImpl implements
		UploadPropositionFilterBusinessService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFilterBusinessServiceImpl.class);

	private final UploadPropositionFilterRepository repository;

	private final UploadPropositionRuleRepository ruleRepository;

	private final UploadPropositionActionRepository actionRepository;

	public UploadPropositionFilterBusinessServiceImpl(
			UploadPropositionFilterRepository repository,
			UploadPropositionRuleRepository ruleRepository,
			UploadPropositionActionRepository actionRepository) {
		super();
		this.repository = repository;
		this.ruleRepository = ruleRepository;
		this.actionRepository = actionRepository;
	}

	@Override
	public UploadPropositionFilter find(String uuid) {
		return repository.find(uuid);
	}

	@Override
	public List<UploadPropositionFilter> findAll() {
		return repository.findAll();
	}

	@Override
	public List<UploadPropositionFilter> findAllEnabledFilters() {
		return repository.findAllEnabledFilters();
	}

	@Override
	public UploadPropositionFilter create(UploadPropositionFilter dto)
			throws BusinessException {
		logger.debug(dto.toString());
		UploadPropositionFilter filter = new UploadPropositionFilter();
		filter.setEnable(dto.isEnable());
		filter.setMatch(dto.getMatch());
		filter.setName(dto.getName());
		filter.setOrder(dto.getOrder());
		filter.setDomain(dto.getDomain());
		int size = findAll().size();
		filter.setOrder(size);
		UploadPropositionFilter entity = repository.create(filter);
		for (UploadPropositionRule rule : dto.getRules()) {
			rule.setFilter(entity);
			entity.getRules().add(rule);
			ruleRepository.create(rule);
		}
		for (UploadPropositionAction action : dto.getActions()) {
			action.setFilter(entity);
			entity.getActions().add(action);
			actionRepository.create(action);
		}
		return entity;
	}

	@Override
	public UploadPropositionFilter update(UploadPropositionFilter dto)
			throws BusinessException {
		logger.debug(dto.toString());
		UploadPropositionFilter entity = find(dto.getUuid());
		entity.setEnable(dto.isEnable());
		entity.setMatch(dto.getMatch());
		entity.setName(dto.getName());
		entity.setOrder(dto.getOrder());
		entity = repository.update(entity);

		resetRulesAndActions(entity);
		entity = repository.update(entity);
		for (UploadPropositionRule rule : dto.getRules()) {
			rule.setFilter(entity);
			entity.getRules().add(rule);
			ruleRepository.create(rule);
		}
		for (UploadPropositionAction action : dto.getActions()) {
			action.setFilter(entity);
			entity.getActions().add(action);
			actionRepository.create(action);
		}
		return entity;
	}

	@Override
	public void delete(UploadPropositionFilter entity) throws BusinessException {
		resetRulesAndActions(entity);
		repository.delete(entity);
	}

	private void resetRulesAndActions(UploadPropositionFilter entity)
			throws BusinessException {
		for (UploadPropositionRule rule : entity.getRules()) {
			ruleRepository.delete(rule);
		}
		for (UploadPropositionAction action : entity.getActions()) {
			actionRepository.delete(action);
		}
	}

}
