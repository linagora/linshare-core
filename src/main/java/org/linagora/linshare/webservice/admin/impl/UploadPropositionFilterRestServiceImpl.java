package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadPropositionFilterFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.UploadPropositionFilterRestService;
import org.linagora.linshare.webservice.dto.UploadPropositionFilterDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

/*
 * TODO:
 * - swagger documentation
 */
@Path("/upload_proposition_filters")
@Api(value = "/rest/admin/upload_proposition_filters", description = "Upload proposition filters API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadPropositionFilterRestServiceImpl extends WebserviceBase
		implements UploadPropositionFilterRestService {

	private UploadPropositionFilterFacade uploadPropositionFilterFacade;

	public UploadPropositionFilterRestServiceImpl(
			UploadPropositionFilterFacade uploadPropositionFilterFacade) {
		super();
		this.uploadPropositionFilterFacade = uploadPropositionFilterFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all filters.", response = UploadPropositionFilterDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin.") })
	@Override
	public List<UploadPropositionFilterDto> findAll() throws BusinessException {
		return uploadPropositionFilterFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a filter", response = UploadPropositionFilterDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin.") })
	@Override
	public UploadPropositionFilterDto find(@PathParam("uuid") String uuid)
			throws BusinessException {
		return uploadPropositionFilterFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create a filter.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin.") })
	@Override
	public UploadPropositionFilterDto create(UploadPropositionFilterDto filter)
			throws BusinessException {
		return uploadPropositionFilterFacade.create(filter);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a filter.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't super admin.") })
	@Override
	public UploadPropositionFilterDto update(UploadPropositionFilterDto filter)
			throws BusinessException {
		return uploadPropositionFilterFacade.update(filter);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a filter.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void delete(UploadPropositionFilterDto filter) throws BusinessException {
		uploadPropositionFilterFacade.delete(filter.getUuid());
	}

}
