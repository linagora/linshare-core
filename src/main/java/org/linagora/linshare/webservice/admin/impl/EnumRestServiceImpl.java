/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.webservice.admin.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.EnumRestService;
import org.linagora.linshare.webservice.utils.EnumResourceUtils;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.Operation;


@Path("/enums")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class EnumRestServiceImpl extends WebserviceBase implements EnumRestService {

	@Path("/")
	@HEAD
	@Operation(summary = "Find all enums available.")
	@Override
	public Response findAll(@Context UriInfo info) throws BusinessException {
		List<Link> res = Lists.newArrayList();

		// Iterate over all enums under ENUMS_PATH package
		for (String name : new EnumResourceUtils().getAllEnumsName()) {
			String uri = getContextPath(info) + name;

			res.add(Link.fromUri(uri).rel(name).build());
		}
		return Response.ok().links(res.toArray(new Link[res.size()])).build();
	}

	@Path("/{enum}")
	@OPTIONS
	@Operation(summary = "Find all values for an enum.")
	@Override
	public Response options(@PathParam("enum") String enumName)
			throws BusinessException {
		List<String> res = new EnumResourceUtils().findEnumConstants(enumName);

		return Response.ok(new GenericEntity<List<String>>(res) {}).build();
	}

	private String getContextPath(UriInfo info) {
		String contextPath = info.getAbsolutePath().toString();

		if (!contextPath.endsWith("/"))
			contextPath += '/';
		return contextPath;
	}

	@Path("/")
	@GET
	@Operation(summary = "Find all available enums.")
	@Override
	public List<String> findAll() throws BusinessException {
		return new EnumResourceUtils().getAllEnumsName();
	}
}
