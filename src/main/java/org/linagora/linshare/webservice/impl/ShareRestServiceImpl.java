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

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.webservice.ShareRestService;
import org.linagora.linshare.webservice.dto.ShareDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShareRestServiceImpl extends WebserviceBase implements ShareRestService{

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
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON}) // application/xml application/json 
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
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		} 
		
		try {
			webServiceShareFacade.sharedocument(targetMail, uuid, securedShare);
		} catch (BusinessException e) {
			throw analyseFaultREST(e);
		}
	}
}
