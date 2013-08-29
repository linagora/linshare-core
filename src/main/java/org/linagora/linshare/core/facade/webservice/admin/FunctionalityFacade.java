package org.linagora.linshare.core.facade.webservice.admin;

import java.util.List;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

public interface FunctionalityFacade extends AdminGenericFacade {

	FunctionalityDto get(String domain, String identifier)
			throws BusinessException;

	List<FunctionalityDto> getAll(String domain) throws BusinessException;

	void update(String domain, FunctionalityDto func) throws BusinessException;
	
	void delete(String domain, FunctionalityDto func) throws BusinessException;

}
