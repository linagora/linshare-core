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
package org.linagora.linshare.webservice.uploadrequestv4.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.mongo.entities.ChangeUploadRequestUrlPassword;
import org.linagora.linshare.webservice.uploadrequestv4.ChangeUploadRequestPasswordUrlRestService;

import io.swagger.v3.oas.annotations.Parameter;


@Path("/password")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class ChangeUploadRequestUrlPasswordRestServiceImpl implements ChangeUploadRequestPasswordUrlRestService {

	protected UploadRequestUrlFacade requestUrlFacade;

	public ChangeUploadRequestUrlPasswordRestServiceImpl(
			UploadRequestUrlFacade requestUrlFacade) {
		super();
		this.requestUrlFacade = requestUrlFacade;
	}

	@PUT
	@Path("/{uuid}")
	@Override
	public void changePassword(
			@Parameter(description = "Uplaod request url uuid to update", required = true) @PathParam(value = "uuid") String uuid,
			ChangeUploadRequestUrlPassword reset) throws BusinessException {
		requestUrlFacade.changePassword(uuid, reset);
	}

}
