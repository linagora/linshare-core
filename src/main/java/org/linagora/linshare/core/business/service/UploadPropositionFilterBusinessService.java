package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadPropositionFilterBusinessService {

	UploadPropositionFilter find(String uuid);
	
	List<UploadPropositionFilter> findAll();

	UploadPropositionFilter create(UploadPropositionFilter entity)
			throws BusinessException;

	UploadPropositionFilter update(UploadPropositionFilter entity)
			throws BusinessException;

	void delete(UploadPropositionFilter entity) throws BusinessException;
}
