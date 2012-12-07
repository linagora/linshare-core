/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linshare.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.commons.httpclient.HttpStatus;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.webservice.ShareRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShareRestServiceImpl extends WebserviceBase implements ShareRestService{

	
	
	private static final Logger logger = LoggerFactory.getLogger(ShareRestServiceImpl.class);
	
	private final WebServiceShareFacade webServiceShareFacade;
	
	
	public ShareRestServiceImpl(final WebServiceShareFacade facade){
		this.webServiceShareFacade = facade;
	}

	
	@GET
    @Path("/sharedocument/{targetMail}/{uuid}")
	@Override
	public void sharedocument(@PathParam("targetMail") String targetMail, @PathParam("uuid") String uuid, @DefaultValue("0") @QueryParam("securedShare") int securedShare) {
		User actor;
		try {
			actor = webServiceShareFacade.checkAuthentication();
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		} 
		
		if ((actor instanceof Guest  && !actor.getCanUpload())) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN,"You are not authorized to use this service");
		}
		
		try {
			webServiceShareFacade.sharedocument(targetMail, uuid, securedShare);
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
	}
	
	
	@POST
    @Path("/multiplesharedocuments")
	@Override
	public void multiplesharedocuments(@FormParam("targetMail") String targetMail, @FormParam("file") List<String> uuid, @FormParam("securedShare") @DefaultValue("0") int securedShare, @FormParam("message")  @DefaultValue("") String message) {
		
		User actor;
		
		try {
			actor = webServiceShareFacade.checkAuthentication();
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		} 
 
		if ((actor instanceof Guest  && !actor.getCanUpload())) {
			throw giveRestException(HttpStatus.SC_FORBIDDEN,"You are not authorized to use this service");
		}
		
		
		//check not empty values
		List<String> uuidValues = new ArrayList<String>();
		for (String identifier : uuid) {
			if(identifier!=null && !identifier.isEmpty()) uuidValues.add(identifier);
		}
		if(uuidValues.size()==0){
			throw giveRestException(HttpStatus.SC_BAD_REQUEST, "Missing parameter file");
		}
		
 
		try {
			webServiceShareFacade.multiplesharedocuments(targetMail, uuidValues, securedShare, message);
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
 
	}
	

}
