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
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainQuotaFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.DomainQuotaDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.DomainQuotaRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Api(value = "/rest/admin/quotas", description = "Quota administration service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Path("/quotas")
public class DomainQuotaRestServiceImpl extends WebserviceBase implements DomainQuotaRestService {

	private DomainQuotaFacade facade;

	public DomainQuotaRestServiceImpl(DomainQuotaFacade facade) {
		this.facade = facade;
	}

	@Path("/domains")
	@GET
	@ApiOperation(value = "Return domain quotas.", response = DomainQuotaDto.class, responseContainer="List")
	@Override
	public List<DomainQuotaDto> findAll(
			@ApiParam(value = "Filter domain quotas by its parent uuid.", required = false) 
				@QueryParam("parent") String parentUuid) throws BusinessException {
		return facade.findAll(parentUuid);
	}

	@Path("/domains/{uuid}")
	@GET
	@ApiOperation(value = "find domain quota", response = DomainQuotaDto.class)
	@Override
	public DomainQuotaDto find(
			@ApiParam(value = "Domain quota Uuid", required = true) @PathParam("uuid") String uuid) throws BusinessException {
		return facade.find(uuid);
	}

	@Path("/domains/{uuid : .*}")
	@PUT
	@ApiOperation(value = "Update a domain quota", response = DomainQuotaDto.class)
	@Override
	public DomainQuotaDto update(
			@ApiParam(value = "Domain quota to update. Only quota, override and maintenance field can be updated. If null they will be ignored.", required = true) DomainQuotaDto dto,
			@ApiParam(value = "Domain quota Uuid, if null dto.uuid is used.", required = false)
				@PathParam("uuid") String uuid) throws BusinessException {
		return facade.update(dto, uuid);
	}

}
