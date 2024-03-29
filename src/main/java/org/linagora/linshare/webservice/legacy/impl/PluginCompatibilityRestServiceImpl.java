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
package org.linagora.linshare.webservice.legacy.impl;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.collections.CollectionUtils;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.SimpleStringValue;
import org.linagora.linshare.core.facade.webservice.user.AccountQuotaFacade;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.core.utils.StringPredicates;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.legacy.PluginCompatibilityRestService;
import org.linagora.linshare.webservice.utils.WebServiceUtils;

public class PluginCompatibilityRestServiceImpl extends WebserviceBase implements PluginCompatibilityRestService {

	private final DocumentFacade webServiceDocumentFacade;
	private final ShareFacade webServiceShareFacade;
	private final AccountQuotaFacade accountQuotaFacade;

	public PluginCompatibilityRestServiceImpl(
			final DocumentFacade webServiceDocumentFacade,
			final ShareFacade facade,
			final AccountQuotaFacade accountQuotaFacade) {
		this.webServiceDocumentFacade = webServiceDocumentFacade;
		this.webServiceShareFacade = facade;
		this.accountQuotaFacade = accountQuotaFacade;
	}

	@GET
	@Path("/plugin/information")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public SimpleStringValue getInformation() {
		return new SimpleStringValue("api-version-1");
	}

	@POST
	@Path("/share/multiplesharedocuments")
	@Override
	public void multiplesharedocuments(@FormParam("targetMail") String targetMail, @FormParam("file") List<String> uuid,
			@FormParam("securedShare") @DefaultValue("0") int securedShare, @FormParam("message") @DefaultValue("") String message,
			@FormParam("inReplyTo") @DefaultValue("") String inReplyTo, @FormParam("references") @DefaultValue("") String references)
			throws BusinessException {
		CollectionUtils.filter(uuid, StringPredicates.isNotBlank());
		if (uuid.isEmpty())
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("Missing parameter file").build());
		webServiceShareFacade.multiplesharedocuments(targetMail, uuid, securedShare, message, inReplyTo, references);
	}

	/**
	 * upload a file in user's space. send file inside a form
	 */
	@POST
	@Path("/document/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Override
	public DocumentDto uploadfile(@Multipart(value = "file") InputStream theFile,
			@Multipart(value = "description", required = false) String description,
			@Multipart(value = "filename", required = false) String givenFileName, MultipartBody body) throws BusinessException {
		checkMaintenanceMode();
		String comment = (description == null) ? "" : description;
		if (theFile == null) {
			throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
					.entity("Missing file (check parameter file)").build());
		}
		String fileName = getFileName(givenFileName, body);
		File tempFile = WebServiceUtils.getTempFile(theFile, "rest-plugin", fileName);
		try {
			return webServiceDocumentFacade.create(tempFile, fileName, comment, null);
		} finally {
			WebServiceUtils.deleteTempFile(tempFile);
		}
	}

	private void checkMaintenanceMode() {
		boolean maintenance = accountQuotaFacade.maintenanceModeIsEnabled();
		if (maintenance) {
			 // HTTP error 501
			throw new BusinessException(
					BusinessErrorCode.MODE_MAINTENANCE_ENABLED,
					"Maintenance mode is enable, uploads are disabled.");
		}
	}
}
