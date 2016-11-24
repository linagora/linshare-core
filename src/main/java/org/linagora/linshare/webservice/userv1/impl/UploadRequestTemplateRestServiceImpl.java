/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2016 LINAGORA
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

package org.linagora.linshare.webservice.userv1.impl;

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

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.UploadRequestDto;
import org.linagora.linshare.core.facade.webservice.user.UploadRequestTemplateFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.UploadRequestTemplateDto;
import org.linagora.linshare.webservice.userv1.UploadRequestTemplateRestService;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/upload_request_templates")
@Api(value = "/rest/user/upload_request_templates", description = "upload request templates API")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class UploadRequestTemplateRestServiceImpl implements UploadRequestTemplateRestService {

	private UploadRequestTemplateFacade uploadRequestTemplateFacade;

	public UploadRequestTemplateRestServiceImpl(final UploadRequestTemplateFacade uploadRequestTemplateFacade) {
		this.uploadRequestTemplateFacade = uploadRequestTemplateFacade;
	}

	@GET
	@Path("/")
	@ApiOperation(value = "Find an upload request template.", response = UploadRequestTemplateDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public List<UploadRequestTemplateDto> findAll() throws BusinessException {
		return uploadRequestTemplateFacade.findAll(null);
	}

	@GET
	@Path("/{uuid}")
	@ApiOperation(value = "Find an upload request template.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestTemplateDto find(
			@ApiParam(value = "Upload request template uuid.", required = true) @PathParam(value = "uuid") String uuid)
					throws BusinessException {
		return uploadRequestTemplateFacade.find(null, uuid);
	}

	@POST
	@Path("/")
	@ApiOperation(value = "Create an upload request.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestTemplateDto create(
			@ApiParam(value = "Upload request.", required = true) UploadRequestTemplateDto templateDto)
					throws BusinessException {
		UploadRequestTemplateDto dto = uploadRequestTemplateFacade.create(null, templateDto);
		return dto;
	}

	@PUT
	@Path("/{uuid}")
	@ApiOperation(value = "Update an upload request template.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestTemplateDto update(
			@ApiParam(value = "Upload request uuid.", required = true) @PathParam(value = "uuid") String uuid,
			@ApiParam(value = "Upload request.", required = true) UploadRequestTemplateDto templateDto)
					throws BusinessException {
		UploadRequestTemplateDto dto = uploadRequestTemplateFacade.update(null, uuid, templateDto);
		return dto;
	}

	@DELETE
	@Path("/{uuid}")
	@ApiOperation(value = "Delete an upload request template.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestTemplateDto delete(
			@ApiParam(value = "Upload request uuid.", required = true) @PathParam(value = "uuid") String uuid)
					throws BusinessException {
		UploadRequestTemplateDto dto = uploadRequestTemplateFacade.delete(null, uuid);
		return dto;
	}

	@DELETE
	@Path("/")
	@ApiOperation(value = "Delete an upload request template.", response = UploadRequestDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "Authentication failed."),
			@ApiResponse(code = 401, message = "Unauthorized."), @ApiResponse(code = 404, message = "Not found.") })
	@Override
	public UploadRequestTemplateDto delete(
			@ApiParam(value = "Upload request.", required = true) UploadRequestTemplateDto dto)
					throws BusinessException {
		Validate.notNull(dto, "Template must be set.");
		return delete(dto.getUuid());
	}

}
