package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

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
import org.linagora.linshare.core.facade.webservice.admin.MailActivationFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.FunctionalityAdminDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailActivationAdminDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.MailActivationRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/mail_activations")
@Api(value = "/rest/admin/mail_activations", description = "Mail activations service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class MailActivationRestServiceImpl extends WebserviceBase implements
		MailActivationRestService {

	protected final MailActivationFacade facade;

	public MailActivationRestServiceImpl(
			MailActivationFacade mailActivationFacade) {
		super();
		this.facade = mailActivationFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all domain's mail activations.", response = FunctionalityAdminDto.class, responseContainer = "List")
	@Override
	public List<MailActivationAdminDto> findAll(
			@ApiParam(value = "Domain identifier.", required = false) @QueryParam(value = "domainId") String domainId)
			throws BusinessException {
		return facade.findAll(domainId);
	}

	@Path("/{mailActivationId}")
	@GET
	@ApiOperation(value = "Find a domain's mail activations.", response = FunctionalityAdminDto.class)
	@Override
	public MailActivationAdminDto find(
			@ApiParam(value = "Domain identifier.", required = false) @QueryParam(value = "domainId") String domainId,
			@ApiParam(value = "Mail activation identifier.", required = true) @PathParam(value = "mailActivationId") String mailActivationId)
			throws BusinessException {
		return facade.find(domainId, mailActivationId);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a domain's mail activations.")
	@Override
	public MailActivationAdminDto update(MailActivationAdminDto mailActivation)
			throws BusinessException {
		return facade.update(mailActivation);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a domain's mail activations.")
	@Override
	public void delete(MailActivationAdminDto mailActivation)
			throws BusinessException {
		facade.delete(mailActivation);
	}

}
