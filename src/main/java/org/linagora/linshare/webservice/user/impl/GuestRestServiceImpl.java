package org.linagora.linshare.webservice.user.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.GuestFacade;
import org.linagora.linshare.webservice.dto.UserDto;
import org.linagora.linshare.webservice.user.GuestRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/guests")
@Api(value = "/rest/guests", description = "Guests service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class GuestRestServiceImpl implements GuestRestService {

	private final GuestFacade guestFacade;

	public GuestRestServiceImpl(final GuestFacade guestFacade) {
		this.guestFacade = guestFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all guest of a user.", response = UserDto.class)
	@Override
	public List<UserDto> findAll(String ownerLsUuid) throws BusinessException {
		return null;
	}

	@Path("/{lsUuid}")
	@GET
	@ApiOperation(value = "Find a guest.")
	@Override
	public UserDto find(
			@ApiParam(value = "Guest's lsUuid.", required = true) @PathParam("lsUuid") String lsUuid)
			throws BusinessException {
		return guestFacade.find(lsUuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a guest.")
	@Override
	public UserDto create(
			@ApiParam(value = "Guest to create.", required = true) UserDto guest,
			@ApiParam(value = "Guest owner lsuuid.") @QueryParam("ownerLsUuid") @DefaultValue("null") String ownerLsUuid)
			throws BusinessException {
		return guestFacade.create(guest, ownerLsUuid);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a guest.")
	@Override
	public UserDto update(
			@ApiParam(value = "Guest to update.", required = true) UserDto guest)
			throws BusinessException {
		return guestFacade.update(guest);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a guest.")
	@Override
	public void delete(
			@ApiParam(value = "Guest to delete.", required = true) UserDto guest)
			throws BusinessException {
		guestFacade.delete(guest);
	}

	@Path("/{lsUuid}")
	@DELETE
	@ApiOperation(value = "Delete a guest.")
	@Override
	public void delete(
			@ApiParam(value = "Guest's lsUuid to create.", required = true) @PathParam("lsUuid") String lsUuid)
			throws BusinessException {
		guestFacade.delete(lsUuid);
	}
}