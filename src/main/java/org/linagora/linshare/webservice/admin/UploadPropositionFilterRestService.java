package org.linagora.linshare.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.UploadPropositionFilterDto;

public interface UploadPropositionFilterRestService {

	List<UploadPropositionFilterDto> findAll() throws BusinessException;

	UploadPropositionFilterDto find(String uuid) throws BusinessException;

	UploadPropositionFilterDto create(UploadPropositionFilterDto filter) throws BusinessException;

	UploadPropositionFilterDto update(UploadPropositionFilterDto filter) throws BusinessException;

	void delete(UploadPropositionFilterDto filter) throws BusinessException;

}
