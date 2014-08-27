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
