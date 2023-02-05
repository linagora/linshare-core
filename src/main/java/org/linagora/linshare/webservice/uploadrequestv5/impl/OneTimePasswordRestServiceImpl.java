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
package org.linagora.linshare.webservice.uploadrequestv5.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.uploadrequest.UploadRequestUrlFacade;
import org.linagora.linshare.webservice.uploadrequestv5.OneTimePasswordRestService;
import org.linagora.linshare.webservice.uploadrequestv5.dto.OneTimePasswordDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Path("/otp")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
public class OneTimePasswordRestServiceImpl implements OneTimePasswordRestService {

	protected final UploadRequestUrlFacade uploadRequestUrlFacade;

	public OneTimePasswordRestServiceImpl(UploadRequestUrlFacade uploadRequestUrlFacade) {
		super();
		this.uploadRequestUrlFacade = uploadRequestUrlFacade;
	}

	@POST
	@Path("/")
	@Operation(summary = "Create a one time password to download an upload request entry.")
	@Override
	public OneTimePasswordDto create(
			@Parameter(description = "the password that protect this upload request.", required = false)
				@HeaderParam("linshare-uploadrequest-password") String password,
			OneTimePasswordDto otp
			) throws BusinessException {
		return uploadRequestUrlFacade.create(password, otp);
	}

}
