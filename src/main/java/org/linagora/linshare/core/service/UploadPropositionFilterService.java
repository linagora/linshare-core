package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UploadPropositionFilter;
import org.linagora.linshare.core.exception.BusinessException;

public interface UploadPropositionFilterService {

	UploadPropositionFilter find(Account actor, String uuid) throws BusinessException;

	List<UploadPropositionFilter> findAll(Account actor) throws BusinessException;

	List<UploadPropositionFilter> findAllEnabledFilters(Account actor) throws BusinessException;

	UploadPropositionFilter create(Account actor, UploadPropositionFilter dto) throws BusinessException;

	UploadPropositionFilter update(Account actor, UploadPropositionFilter dto) throws BusinessException;

	void delete(Account actor, String uuid) throws BusinessException;

}
