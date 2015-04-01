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
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Path("/enums")
@Api(value = "/rest/admin/enums", description = "Enums service.")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class EnumRestServiceImpl extends WebserviceBase implements EnumRestService {

	@Path("/")
	@HEAD
	@ApiOperation(value = "Find all enums available.")
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
	@ApiOperation(value = "Find all values for an enum.")
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
}
