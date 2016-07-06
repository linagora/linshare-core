/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2016. Contribute to
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.AutocompleteFacade;
import org.linagora.linshare.core.facade.webservice.admin.UserFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.InconsistentSearchDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.UpdateUsersEmailStateDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserSearchDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.admin.UserRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;


@Api(value = "/rest/admin/users", description = "User administration service.")
@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
@Path("/users")
public class UserRestServiceImpl extends WebserviceBase implements
		UserRestService {

	private static final Logger logger = LoggerFactory.getLogger(UserRestServiceImpl.class);

	private final UserFacade userFacade;

	private final AutocompleteFacade autocompleteFacade;

	public UserRestServiceImpl(final UserFacade userFacade,
			final AutocompleteFacade autocompleteFacade) {
		this.userFacade = userFacade;
		this.autocompleteFacade = autocompleteFacade;
	}

	@Path("/search")
	@POST
	@ApiOperation(value = "Search all users who match with patterns.", response = UserDto.class, responseContainer = "Set")
	@Override
	public List<UserDto> search(
			@ApiParam(value = "Patterns to search.", required = true) UserSearchDto userSearchDto)
			throws BusinessException {
		if (lessThan3Char(userSearchDto.getFirstName()) && lessThan3Char(userSearchDto.getLastName()) && lessThan3Char(userSearchDto.getMail())) {
			logger.info("Search request less than 3 char");
			return Lists.newArrayList();
		}
		return userFacade.search(userSearchDto);
	}

	@Path("/search/internals/{pattern}")
	@GET
	@ApiOperation(value = "Search among internal users.", response = UserDto.class, responseContainer = "Set")
	@Override
	public Set<UserDto> searchInternals(
			@ApiParam(value = "Internal users to search for.", required = true) @PathParam("pattern") String pattern)
			throws BusinessException {
		if (pattern.length() < 3) {
			logger.info("Search request less than 3 char");
			return Sets.newHashSet();
		}
		return userFacade.searchInternals(pattern);
	}

	@Path("/search/guests/{pattern}")
	@GET
	@ApiOperation(value = "Search among guests.", response = UserDto.class, responseContainer = "Set")
	@Override
	public Set<UserDto> searchGuests(
			@ApiParam(value = "Guests to search for.", required = true) @PathParam("pattern") String pattern)
			throws BusinessException {
		if (pattern.length() < 3) {
			logger.info("Search request less than 3 char");
			return Sets.newHashSet();
		}
		return userFacade.searchGuests(pattern);
	}

	@Path("/autocomplete/{pattern}")
	@GET
	@ApiOperation(value = "Provide user autocompletion.", response = UserDto.class, responseContainer = "Set")
	@Override
	public Set<UserDto> autocomplete(
			@ApiParam(value = "Pattern to complete.", required = true) @PathParam("pattern") String pattern)
			throws BusinessException {
		return autocompleteFacade.findUser(pattern);
	}

	@Path("/{uuid}")
	@GET
	@ApiOperation(value = "Find a user.", response = UserDto.class)
	@Override
	public UserDto find(
			@ApiParam(value = "User uuid.", required = true) @PathParam("uuid") String uuid)
			throws BusinessException {
		return userFacade.findUser(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Find a user.")
	@Override
	public void head(
			@ApiParam(value = "User uuid.", required = true) @PathParam("uuid") String uuid)
					throws BusinessException {
		userFacade.findUser(uuid);
	}

	@Path("/{uuid}")
	@HEAD
	@ApiOperation(value = "Test if an user exists.")
	@Override
	public void exist(@ApiParam(value = "User uuid.", required = true) @PathParam("uuid") String uuid) throws BusinessException {
		if (!userFacade.exist(uuid)) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The current uuid does not refer to an existing user profile.");
		}
	}

	@Path("/")
	@POST
	@ApiOperation(value = "Create an user if he exists in some ldap directories.")
	@Override
	public UserDto create(@ApiParam(value = "User to update", required = true) UserDto user) throws BusinessException {
		return userFacade.create(user);
	}

	@Path("/")
	@PUT
	@ApiOperation(value = "Update an user.")
	@Override
	public UserDto update(
			@ApiParam(value = "User to update", required = true) UserDto userDto)
			throws BusinessException {
		return userFacade.update(userDto);
	}

	@Path("/")
	@DELETE
	@ApiOperation(value = "Delete an user.", response = UserDto.class)
	@Override
	public UserDto delete(
			@ApiParam(value = "User to delete.", required = true) UserDto userDto)
			throws BusinessException {
		return userFacade.delete(userDto);
	}

	@Path("/inconsistent")
	@DELETE
	@ApiOperation(value = "Delete an inconsistent user.", response = UserDto.class)
	@Override
	public UserDto deleteInconsistent(
			@ApiParam(value = "User to delete.", required = true) UserDto userDto)
			throws BusinessException {
		return userFacade.delete(userDto);
	}

	@Path("/inconsistent")
	@GET
	@ApiOperation(value = "Find all inconsistent users.", response = UserDto.class, responseContainer = "Set")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public Set<UserDto> findAllInconsistent() throws BusinessException {
		return userFacade.findAllInconsistent();
	}

	@Path("/inconsistent")
	@PUT
	@ApiOperation(value = "Update an inconsistent user's domain.")
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't admin.") })
	@Override
	public void updateInconsistent(
			@ApiParam(value = "Inconsistent user to update.", required = true) UserDto userDto)
			throws BusinessException {
		userFacade.updateInconsistent(userDto);
	}

	@POST
	@Path("/inconsistent/check")
	@ApiOperation(value = "Generate a report on the adress userMail.", response = InconsistentSearchDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public List<InconsistentSearchDto> check(UserSearchDto dto) {
		return userFacade.checkInconsistentUserStatus(dto);
	}

	@POST
	@Path("/inconsistent/autocomplete")
	@ApiOperation(value = "Autocomplete email on every possible and available data.", response = InconsistentSearchDto.class)
	@ApiResponses({ @ApiResponse(code = 403, message = "User isn't a super admin.") })
	@Override
	public List<String> autocompleteInconsistent(UserSearchDto dto) throws BusinessException {
		return userFacade.autocompleteInconsistent(dto);
	}

	private boolean lessThan3Char(String s) {
		return StringUtils.trimToEmpty(s).length() < 3;
	}

	@Path("/mail_migration")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@ApiOperation(value = "Update users email address.")
	@ApiResponses({
			@ApiResponse(code = 403, message = "User isn't admin"),
			@ApiResponse(code = 400, message = "Bad request : missing required fields."),
			@ApiResponse(code = 500, message = "Internal server error.") })
	@Override
	public UpdateUsersEmailStateDto updateUsersEmail(
			@ApiParam(value = "File stream.", required = true) @Multipart(value = "file", required = true) InputStream file,
			@ApiParam(value = "The given file name of the uploaded file.", required = false) @Multipart(value = "filename", required = false) String givenFileName,
			@ApiParam(value = "The given field delimiter of the csv file.", required = false) @Multipart(value = "csvFieldDelimiter", required = false) String csvFieldDelimiter,
			MultipartBody body) throws BusinessException {
		if (file == null) {
			logger.error("Missing file (check parameter file)");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"Missing file (check parameter file)");
		}
		String fileName = getFileName(givenFileName, body);
		String extension = null;
		String csvExtension = ".csv";
		int splitIdx = fileName.lastIndexOf('.');
		if (splitIdx > -1) {
			extension = fileName.substring(splitIdx, fileName.length());
		}
		if (!extension.equals(csvExtension)) {
			logger.error("Bad file extension");
			throw giveRestException(HttpStatus.SC_BAD_REQUEST,
					"bad file extension");
		}
		File tempFile = getTempFile(file, "emails-migration", fileName);
		BufferedReader reader = null;
		String csvLine = "";
		String[] emails = null;
		String currentEmail = "";
		String newEmail = "";

		long total = 0;
		long updated = 0;
		long notUpdated = 0;
		long skipped = 0;

		if(csvFieldDelimiter == null) {
			csvFieldDelimiter = ";";
		}
		UpdateUsersEmailStateDto state = new UpdateUsersEmailStateDto();
		try {
			reader = new BufferedReader(new FileReader(tempFile));
			while ((csvLine = reader.readLine()) != null) {
				emails = csvLine.split(csvFieldDelimiter);
				currentEmail = emails[0];
				newEmail = emails[1];

				if (currentEmail.equals(newEmail)) {
					logger.debug("The former email : " + currentEmail
							+ " is the same to new one : " + newEmail);
					skipped++;
					total++;
					continue;
				}

				boolean user = userFacade.updateEmail(currentEmail, newEmail);
				if (user) {
					updated++;
				}
				total++;
			}

			notUpdated = total - (updated + skipped);
			state.setTotal(total);
			state.setUpdated(updated);
			state.setNotUpdated(notUpdated);
			state.setSkipped(skipped);
			reader.close();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE,
					e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new BusinessException(
					BusinessErrorCode.FILE_INVALID_INPUT_TEMP_FILE,
					e.getMessage());
		} finally {
			deleteTempFile(tempFile);
		}
		return state;
	}
}
