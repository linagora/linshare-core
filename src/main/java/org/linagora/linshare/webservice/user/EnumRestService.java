package org.linagora.linshare.webservice.user;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.linagora.linshare.core.exception.BusinessException;

public interface EnumRestService {

	Response get(UriInfo info) throws BusinessException;

	Response options(String enumName) throws BusinessException;
}
