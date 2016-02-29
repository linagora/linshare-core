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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.LdapConnectionFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.LdapConnectionDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.LDAPConnectionRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/ldap_connections")
@Api(value = "/rest/admin/ldap_connections", description = "Ldap connections service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class LDAPConnectionRestServiceImpl extends WebserviceBase implements
		LDAPConnectionRestService {

	private final LdapConnectionFacade ldapConnectionFacade;

	public LDAPConnectionRestServiceImpl(
			final LdapConnectionFacade ldapConnectionFacade) {
		this.ldapConnectionFacade = ldapConnectionFacade;
	}

	@Path("/")
	@GET
	@ApiOperation(value = "Find all LDAP connections.", response = LdapConnectionDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public Set<LdapConnectionDto> findAll() throws BusinessException {
		return ldapConnectionFacade.findAll();
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a LDAP connection.", response = LdapConnectionDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public LdapConnectionDto find(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return ldapConnectionFacade.find(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a LDAP connection.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public void head(@PathParam(value = "uuid") String uuid) throws BusinessException {
		ldapConnectionFacade.find(uuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Find a LDAP connection.", response = LdapConnectionDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public LdapConnectionDto create(LdapConnectionDto LDAPConnection)
			throws BusinessException {
		return ldapConnectionFacade.create(LDAPConnection);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update a LDAP connection.", response = LdapConnectionDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public LdapConnectionDto update(LdapConnectionDto LDAPConnection)
			throws BusinessException {
		return ldapConnectionFacade.update(LDAPConnection);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete a LDAP connection.", response = LdapConnectionDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public LdapConnectionDto delete(LdapConnectionDto LDAPConnection)
			throws BusinessException {
		return ldapConnectionFacade.delete(LDAPConnection);
	}
}
