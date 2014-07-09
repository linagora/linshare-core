/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2014 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import com.wordnik.swagger.annotations.*;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.UploadRequestHistoryFacade;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.UploadRequestHistoryRestService;
import org.linagora.linshare.webservice.dto.UploadRequestHistoryDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

@Path("/upload_requests")
@Api(value = "/rest/admin/upload_requests", description = "History requests API")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
public class UploadRequestHistoryRestServiceImpl extends WebserviceBase implements UploadRequestHistoryRestService {

	private final UploadRequestHistoryFacade uploadRequestHistoryFacade;

	public UploadRequestHistoryRestServiceImpl(UploadRequestHistoryFacade uploadRequestHistoryFacade) {
		this.uploadRequestHistoryFacade = uploadRequestHistoryFacade;
	}

	@Path("/{requestUuid}")
	@GET
	@ApiOperation(value = "Search all history entries for an upload request.", response = UploadRequestHistoryDto.class)
	@ApiResponses({@ApiResponse(code = 403, message = "User isn't admin.")})
	@Override
	public Set<UploadRequestHistoryDto> findAll(@PathParam(value = "requestUuid") String requestUuid) throws BusinessException {
		return uploadRequestHistoryFacade.findAll(requestUuid);
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Search all created or enabled history entries.", response = UploadRequestHistoryDto.class)
	@ApiResponses({@ApiResponse(code = 403, message = "User isn't admin.")})
	@Override
	public Set<UploadRequestHistoryDto> findAllByStatus(@ApiParam(value = "Status to search for.", required = true) List<UploadRequestStatus> status) throws BusinessException {
		return uploadRequestHistoryFacade.findAll(status);
	}
}
