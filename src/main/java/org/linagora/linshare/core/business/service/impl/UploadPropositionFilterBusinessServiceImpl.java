package org.linagora.linshare.core.business.service.impl;

import java.util.List;

import org.linagora.linshare.core.business.service.UploadPropositionFilterBusinessService;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.UploadPropositionFilterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPropositionFilterBusinessServiceImpl implements
		UploadPropositionFilterBusinessService {

	private static final Logger logger = LoggerFactory
			.getLogger(UploadPropositionFilterBusinessServiceImpl.class);

	private final UploadPropositionFilterRepository repository;

	public UploadPropositionFilterBusinessServiceImpl(
			UploadPropositionFilterRepository repository) {
		super();
		this.repository = repository;
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
	public UploadPropositionFilter create(UploadPropositionFilter entity)
			throws BusinessException {
		return repository.create(entity);
	}

	@Override
	public UploadPropositionFilter update(UploadPropositionFilter entity)
			throws BusinessException {
		return repository.update(entity);
	}

	@Override
	public void delete(UploadPropositionFilter entity) throws BusinessException {
		repository.delete(entity);
	}

}
