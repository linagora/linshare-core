package org.linagora.linshare.webservice.admin.impl;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.FunctionalityFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.FunctionalityRestService;
import org.linagora.linshare.webservice.dto.FunctionalityDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/functionalities")
@Api(value = "/rest/admin/functionalities", description = "Functionalities service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class FunctionalityRestServiceImpl extends WebserviceBase implements
		FunctionalityRestService {

	private final FunctionalityFacade functionalityFacade;

	public FunctionalityRestServiceImpl(
			final FunctionalityFacade functionalityFacade) {
		this.functionalityFacade = functionalityFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all domain's functionalities.", response = FunctionalityDto.class, responseContainer = "Set")
	@Override
	public Set<FunctionalityDto> findAll(
			@QueryParam(value = "domainId") String domainId)
			throws BusinessException {
		return functionalityFacade.findAll(domainId);
	}

	@Path("/{funcId}")
	@GET
	@ApiOperation(value = "Find a domain's functionality.", response = FunctionalityDto.class)
	@Override
	public FunctionalityDto find(
			@QueryParam(value = "domainId") String domainId,
			@PathParam(value = "funcId") String funcId)
			throws BusinessException {
		return functionalityFacade.find(domainId, funcId);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a domain's functionality.")
	@Override
	public FunctionalityDto update(FunctionalityDto func) throws BusinessException {
		return functionalityFacade.update(func);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a domain's functionality.")
	@Override
	public void delete(FunctionalityDto func) throws BusinessException {
		functionalityFacade.delete(func);
	}
}
