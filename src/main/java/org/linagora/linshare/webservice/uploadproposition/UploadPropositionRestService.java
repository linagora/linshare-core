package org.linagora.linshare.webservice.uploadproposition;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionFilterDto;

public interface UploadPropositionRestService {

	List<UploadPropositionFilterDto> findAllFilters() throws BusinessException;

	Boolean checkIfValidRecipient(String userMail, String userDomain);

	void create(UploadPropositionDto dto) throws BusinessException;
}
