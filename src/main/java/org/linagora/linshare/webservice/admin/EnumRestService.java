package org.linagora.linshare.webservice.admin;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.linagora.linshare.core.exception.BusinessException;

public interface EnumRestService {

	Response findAll(UriInfo info) throws BusinessException;

	Response options(String enumName) throws BusinessException;
}
