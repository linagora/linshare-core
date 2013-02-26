package org.linagora.linshare.webservice.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.webservice.dto.SimpleStringValue;
import org.linagora.linshare.webservice.PluginCompatibilityRestService;

public class PluginCompatibilityRestServiceImpl extends WebserviceBase implements PluginCompatibilityRestService {

	private final WebServiceShareFacade webServiceShareFacade;

	public PluginCompatibilityRestServiceImpl(final WebServiceShareFacade facade){
		this.webServiceShareFacade = facade;
	}
	
	@GET
    @Path("/plugin/information")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Override
	public SimpleStringValue getInformation() {
		return new SimpleStringValue("undefined");
	}
	
	@POST
    @Path("/share/multiplesharedocuments")
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
