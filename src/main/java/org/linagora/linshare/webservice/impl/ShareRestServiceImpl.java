/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.webservice.impl;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.webservice.ShareRestService;
import org.linagora.linshare.webservice.dto.ShareDto;
import org.linagora.linshare.webservice.utils.DocumentStreamReponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShareRestServiceImpl extends WebserviceBase implements ShareRestService{

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ShareRestServiceImpl.class);
	
	private final WebServiceShareFacade webServiceShareFacade;
	
	public ShareRestServiceImpl(final WebServiceShareFacade facade){
		this.webServiceShareFacade = facade;
	}
	
	/**
	 * get the files of the user
	 */
	@Path("/list")
	@GET
	@Produces({MediaType.APPLICATION_XML, "application/json;charset=UTF-8" }) // application/xml application/json 
	@Override
	public List<ShareDto> getReceivedShares()
    {
		List<ShareDto> shares = null;
		
		try {
			webServiceShareFacade.checkAuthentication();
			shares = webServiceShareFacade.getReceivedShares();
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
		
		return shares;
    }
	
	@GET
    @Path("/sharedocument/{targetMail}/{uuid}")
	@Override
	public void sharedocument(@PathParam("targetMail") String targetMail, @PathParam("uuid") String uuid, @DefaultValue("0") @QueryParam("securedShare") int securedShare) {
		try {
			webServiceShareFacade.checkAuthentication();
			webServiceShareFacade.sharedocument(targetMail, uuid, securedShare);
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
	}

	@POST
    @Path("/multiplesharedocuments")
	@Override
	public void multiplesharedocuments(@FormParam("mail") List<String> targetMails, @FormParam("file")  List<String> uuids, @DefaultValue("0") @QueryParam("securedShare") int securedShare, @FormParam("message")  @DefaultValue("") String message) {
		try {
			webServiceShareFacade.checkAuthentication();
			webServiceShareFacade.multiplesharedocuments(targetMails, uuids, securedShare, message);
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
	}

	@Path("/download/{uuid}")
	@GET
	@Override
	public Response getDocumentStream(@PathParam("uuid") String shareUuid) {
		try {
			webServiceShareFacade.checkAuthentication();
			ShareDto shareDto = webServiceShareFacade.getReceivedShare(shareUuid);
			InputStream documentStream = webServiceShareFacade.getDocumentStream(shareUuid);
			ResponseBuilder response = DocumentStreamReponseBuilder.getDocumentResponseBuilder(documentStream, shareDto.getName(), shareDto.getType());
			return response.build();
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
	}
	
	
}
