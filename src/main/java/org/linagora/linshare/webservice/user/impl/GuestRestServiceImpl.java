package org.linagora.linshare.webservice.user.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.webservice.dto.UserDto;
import org.linagora.linshare.webservice.user.GuestRestService;

import com.wordnik.swagger.annotations.Api;

@Path("/guests")
@Api(value = "/rest/guests", description = "Guests service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GuestRestServiceImpl implements GuestRestService {
	
	private final GuestFacade guestFacade;
	
	public GuestRestServiceImpl(final GuestFacade guestFacade) {
		this.guestFacade = guestFacade;
	}

	@Override
	public UserDto find(String lsUuid) throws BusinessException {
		return guestFacade.find(lsUuid);
	}

	@Override
	public UserDto findAll(String ownerLsUuid) throws BusinessException {
		return null;
	}

	@Override
	public UserDto create(UserDto guest) throws BusinessException {
		return guestFacade.create(guest);
	}

	@Override
	public UserDto update(UserDto guest) throws BusinessException {
		return guestFacade.update(guest);
	}

	@Override
	public void delete(UserDto guest) throws BusinessException {
		guestFacade.delete(guest);
	}

	@Override
	public void delete(String lsUuid) throws BusinessException {
		guestFacade.delete(lsUuid);
	}

	@Override
	public List<UserDto> getRestrictedContacts(String lsUuid) throws BusinessException {
		return guestFacade.getRestrictedContacts(lsUuid);
	}
}
