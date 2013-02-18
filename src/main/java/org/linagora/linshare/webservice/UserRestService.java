package org.linagora.linshare.webservice;

import java.util.List;

import javax.ws.rs.Path;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.UserDto;

@Path("/rest/users")
public interface UserRestService {

	public List<UserDto> getUsers() throws BusinessException;
}
