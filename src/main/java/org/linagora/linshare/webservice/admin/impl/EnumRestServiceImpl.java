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
import org.linagora.linshare.webservice.dto.LDAPConnectionDto;
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
