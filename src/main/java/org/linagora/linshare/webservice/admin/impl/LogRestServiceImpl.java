package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.LogEntryFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.LogRestService;
import org.linagora.linshare.webservice.dto.LogCriteriaDto;
import org.linagora.linshare.webservice.dto.LogDto;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/logs")
@Api(value = "/admin/logs", description = "Admin application audit service.")
public class LogRestServiceImpl extends WebserviceBase implements
		LogRestService {

	private LogEntryFacade logEntryFacade;

	public LogRestServiceImpl(final LogEntryFacade logEntryFacade) {
		this.logEntryFacade = logEntryFacade;
	}

	@Path("/")
	@ApiOperation(value = "Search the user history with specified criteria.", response = LogDto.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 401, message = "User isn't admin.") })
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public List<LogDto> query(
			@ApiParam(value = "Criteria to search for.", required = true) LogCriteriaDto criteria)
			throws BusinessException {
		User actor = logEntryFacade.checkAuthentication();

		return logEntryFacade.query(actor, criteria);
	}
}
