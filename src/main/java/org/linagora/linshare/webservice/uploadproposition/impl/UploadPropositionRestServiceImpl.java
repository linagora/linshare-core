package org.linagora.linshare.webservice.uploadproposition.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadproposition.UploadPropositionFacade;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionDto;
import org.linagora.linshare.core.facade.webservice.uploadproposition.dto.UploadPropositionFilterDto;
import org.linagora.linshare.webservice.uploadproposition.UploadPropositionRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/")
@Api(value = "/rest/uploadproposition/filters", description = "filters API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadPropositionRestServiceImpl implements
		UploadPropositionRestService {

	private final UploadPropositionFacade uploadPropositionFacade;

	public UploadPropositionRestServiceImpl(
			UploadPropositionFacade uploadPropositionFilterFacade) {
		super();
		this.uploadPropositionFacade = uploadPropositionFilterFacade;
	}

	@GET
	@Path("/filters")
	@ApiOperation(value = "Find all upload proposition filters.", response = UploadPropositionFilterDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public List<UploadPropositionFilterDto> findAllFilters()
			throws BusinessException {
		return uploadPropositionFacade.findAll();
	}

	@GET
	@Path("/recipient/{userMail}}")
	@ApiOperation(value = "Check if it is a valid user.", response = Boolean.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public Boolean checkIfValidRecipient(
			@PathParam(value = "userMail") String userMail,
			@QueryParam(value = "userDomain") String userDomain) {
		return uploadPropositionFacade.checkIfValidRecipeint(userMail,
				userDomain);
	}

	@POST
	@Path("/propositions")
	@ApiOperation(value = "Check if it is a valid user.")
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed.") })
	@Override
	public void create(UploadPropositionDto dto) throws BusinessException {
		uploadPropositionFacade.create(dto);
	}

}
