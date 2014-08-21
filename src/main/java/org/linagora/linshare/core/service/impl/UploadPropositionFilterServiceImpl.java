package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.business.service.UploadPropositionFilterBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.UploadPropositionFilterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionFilterServiceImpl implements UploadPropositionFilterService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFilterServiceImpl.class);
	
	private final UploadPropositionFilterBusinessService businessService;

	private final DomainBusinessService domainBusinessService;

	public UploadPropositionFilterServiceImpl(UploadPropositionFilterBusinessService businessService, DomainBusinessService domainBusinessService) {
		super();
		this.businessService = businessService;
		this.domainBusinessService = domainBusinessService;
	}

	@Override
	public UploadPropositionFilter find(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "filter uuid is required");
		UploadPropositionFilter filter = businessService.find(uuid);
		if (filter ==null) {
			logger.error(actor.getAccountReprentation() + " is looking for missing filter uuid : " + uuid);
			throw new BusinessException("filter uuid not found.");
		}
		return filter;
	}

	@Override
	public List<UploadPropositionFilter> findAll(Account actor) throws BusinessException {
		preChecks(actor);
		return businessService.findAll();
	}

	@Override
	public UploadPropositionFilter create(Account actor, UploadPropositionFilter dto) throws BusinessException {
		preChecks(actor);
		Validate.notNull(dto, "filter is required");
		dto.setDomain(domainBusinessService.getUniqueRootDomain());
		return businessService.create(dto);
	}

	@Override
	public UploadPropositionFilter update(Account actor, UploadPropositionFilter dto) throws BusinessException {
		preChecks(actor);
		Validate.notNull(dto, "filter is required");
		Validate.notEmpty(dto.getUuid(), "filter uuid is required");
		// permission check
		find(actor, dto.getUuid());
		return businessService.update(dto);
	}

	@Override
	public void delete(Account actor, String uuid) throws BusinessException {
		preChecks(actor);
		Validate.notEmpty(uuid, "filter uuid is required");
		UploadPropositionFilter entity = find(actor, uuid);
		businessService.delete(entity);
	}

	void preChecks(Account actor) {
		Validate.notNull(actor, "actor is required");
		Validate.notEmpty(actor.getLsUuid(), "actor uuid is required");
	}
}
