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
package org.linagora.linshare.webservice.userv2.impl.only;

import java.util.List;

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
import org.linagora.linshare.core.facade.webservice.user.UserPreferenceFacade;
import org.linagora.linshare.mongo.entities.UserPreference;
import org.linagora.linshare.webservice.userv2.UserPreferenceRestService;


@Path("/prefs")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserPreferenceRestServiceImpl implements UserPreferenceRestService {

	protected UserPreferenceFacade facade;

	public UserPreferenceRestServiceImpl(UserPreferenceFacade facade) {
		super();
		this.facade = facade;
	}

	@Path("/")
	@GET
	@Override
	public List<UserPreference> findAll() throws BusinessException {
		return facade.findAll(null);
	}

	@Path("/{uuid}")
	@GET
	@Override
	public UserPreference find(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return facade.find(null, uuid);
	}

	@Path("/")
	@POST
	@Override
	public UserPreference create(UserPreference dto) throws BusinessException {
		return facade.create(null, dto);
	}

	@Path("/")
	@PUT
	@Override
	public UserPreference update(String uuid, UserPreference dto) throws BusinessException {
		return facade.update(null, uuid, dto);
	}

	@Path("/{uuid}")
	@DELETE
	@Override
	public UserPreference delete(@PathParam(value = "uuid") String uuid) throws BusinessException {
		return facade.delete(null, uuid);
	}

	@Path("/")
	@DELETE
	@Override
	public UserPreference delete(UserPreference dto) throws BusinessException {
		return facade.delete(null, dto.getUuid());
	}

}
