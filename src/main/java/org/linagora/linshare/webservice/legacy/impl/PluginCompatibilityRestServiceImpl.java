/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2020.
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
