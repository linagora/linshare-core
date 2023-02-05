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
package org.linagora.linshare.webservice.test.userv4.impl;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.test.user.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.test.user.uploadrequest.UserTestFacade;
import org.linagora.linshare.webservice.test.userv4.UserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRestServiceImpl implements UserRestService {

	protected final Logger logger = LoggerFactory.getLogger(UserRestServiceImpl.class);

	private final UserTestFacade userTestFacade;

	public UserRestServiceImpl(UserTestFacade userTestFacade) {
		super();
		this.userTestFacade = userTestFacade;
	}

	@Path("/")
	@GET
	@Override
	public List<UserDto> findAll() {
		return userTestFacade.findAll();
	}

	@Path("/")
	@POST
	@Override
	public UserDto create(UserDto user) throws BusinessException {
		return userTestFacade.create(user);
	}

}
