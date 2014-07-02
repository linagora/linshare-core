package org.linagora.linshare.webservice.uploadrequest.impl;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.ContactDto;
import org.linagora.linshare.core.facade.webservice.uploadrequest.dto.UploadRequestDto;
import org.linagora.linshare.webservice.uploadrequest.UploadRequestRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/requests")
@Api(value = "/rest/uploadrequest/requests", description = "requests API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestRestServiceImpl implements UploadRequestRestService {

	protected final Logger logger = LoggerFactory
			.getLogger(UploadRequestRestServiceImpl.class);

	private final UploadRequestUrlFacade uploadRequestUrlFacade;


	public UploadRequestRestServiceImpl(
			UploadRequestUrlFacade uploadRequestUrlFacade) {
		super();
		this.uploadRequestUrlFacade = uploadRequestUrlFacade;
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find an upload request.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a technical account.") })
	@Override
	public Response find(@PathParam(value = "uuid") String uuid,
			@HeaderParam("linshare-uploadrequest-password") String password)
			throws BusinessException {
		logger.debug("uuid : " + uuid);
		logger.debug("password : " + password);

		ResponseBuilder response = null;
//		UploadRequestDto data = getTestData();
		UploadRequestDto data = uploadRequestUrlFacade.find(uuid);
		if (data.isProtectedByPassword()) {
			if (password != null && password.trim().equals("fred")) {
				response = Response.ok(data);
			} else {
				response = Response.status(Response.Status.UNAUTHORIZED);
			}
		} else {
			response = Response.ok(data);
		}
		return response.build();
	}

	private UploadRequestDto getTestData() {
		@SuppressWarnings("deprecation")
		UploadRequestDto a = new UploadRequestDto(
				"b344b5ca-d9e7-4857-b959-5e86f34a91f7", new Integer(3),
				new Long(30), new Long(10), new Date(), new Date(2014, 10, 1),
				true, true, "coucou", "my message", false);
		a.setOwner(new ContactDto("Bart", "Simspon",
				"bart.simpson@int1.linshare.dev"));
		a.setRecipient(new ContactDto("Yo", "Da", "yoda@int3.linshare.dev"));
		a.setProtectedByPassword(true);
		return a;
	}

	@POST
	@Path("/")
	@ApiOperation(value = "Find an upload request.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a technical account.") })
	@Override
	public UploadRequestDto update(UploadRequestDto dto)
			throws BusinessException {
		return uploadRequestUrlFacade.close(dto);
	}
}
