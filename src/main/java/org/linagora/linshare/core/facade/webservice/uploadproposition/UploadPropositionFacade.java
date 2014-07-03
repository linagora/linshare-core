package org.linagora.linshare.core.facade.webservice.uploadproposition;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionFilterDto;

public interface UploadPropositionFacade {

	List<UploadPropositionFilterDto> findAll() throws BusinessException;

	boolean checkIfValidRecipeint(String userMail, String userDomain);

	void create(UploadPropositionDto dto) throws BusinessException;

}
