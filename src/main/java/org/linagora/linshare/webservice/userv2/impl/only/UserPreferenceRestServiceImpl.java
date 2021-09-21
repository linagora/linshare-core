/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2016-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
