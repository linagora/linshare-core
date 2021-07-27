/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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
package org.linagora.linshare.webservice.adminv5.impl;

import java.util.Set;

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
import org.linagora.linshare.core.facade.webservice.adminv5.LDAPServerFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.LDAPServerDto;
import org.linagora.linshare.webservice.adminv5.LDAPServerRestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/remote_servers")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class LDAPServerRestServiceImpl implements LDAPServerRestService {

	private final LDAPServerFacade ldapServerFacade;

	
	protected LDAPServerRestServiceImpl(
			LDAPServerFacade lDAPServerFacade) {
		super();
		this.ldapServerFacade = lDAPServerFacade;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all LDAP servers.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = LDAPServerDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public Set<LDAPServerDto> findAll() throws BusinessException {
		return ldapServerFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@Operation(summary = "Find an LDAP server.", responses = {
		@ApiResponse(
			content = @Content(array = @ArraySchema(schema = @Schema(implementation = LDAPServerDto.class))),
			responseCode = "200"
		)
	})
	@Override
	public LDAPServerDto find(
			@Parameter(description = "LDAP server's uuid", required = true)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.find(uuid);
	}

	@Path("/")
	@POST
	@Operation(summary = "Create an LDAP server.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = LDAPServerDto.class)), responseCode = "200")
	})
	@Override
	public LDAPServerDto create(
			@Parameter(description = "The LDAP server to create.", required = false) LDAPServerDto ldapServerDto)
			throws BusinessException {
		return ldapServerFacade.create(ldapServerDto);
	}

	@Path("/{uuid: .*}")
	@PUT
	@Operation(summary = "Update an LDAP server.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = LDAPServerDto.class)), responseCode = "200")
	})
	@Override
	public LDAPServerDto update(
			@Parameter(description = "LDAP server to update", required = false) LDAPServerDto ldapServerDto,
			@Parameter(description = "LDAP server's uuid to update, if null object is used", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.update(uuid, ldapServerDto);
	}

	@Path("/{uuid: .*}")
	@DELETE
	@Operation(summary = "Delete an LDAP server.", responses = {
		@ApiResponse(content = @Content(schema = @Schema(implementation = LDAPServerDto.class)), responseCode = "200")
	})
	@Override
	public LDAPServerDto delete(
			@Parameter(description = "LDAP server to delete.", required = false) LDAPServerDto ldapServerDto,
			@Parameter(description = "LDAP server's uuid to delete, if null object is used", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return ldapServerFacade.delete(uuid, ldapServerDto);
	}
}
