/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */

package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
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

	@Path("/{mailActivationId}")
	@HEAD
	@ApiOperation(value = "Find a domain's mail activations.", response = FunctionalityAdminDto.class)
	@Override
	public void head(
			@ApiParam(value = "Domain identifier.", required = false) @QueryParam(value = "domainId") String domainId,
			@ApiParam(value = "Mail activation identifier.", required = true) @PathParam(value = "mailActivationId") String mailActivationId)
					throws BusinessException {
		facade.find(domainId, mailActivationId);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a domain's mail activations.", response = FunctionalityAdminDto.class)
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
