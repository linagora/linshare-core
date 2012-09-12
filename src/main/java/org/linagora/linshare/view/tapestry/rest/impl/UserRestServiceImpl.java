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
package org.linagora.linshare.view.tapestry.rest.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.rest.UserRestService;
import org.linagora.linshare.view.tapestry.services.impl.MailContainerBuilder;
import org.linagora.linshare.view.tapestry.services.impl.PropertiesSymbolProvider;
import org.linagora.restmarshaller.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.antera.t5restfulws.RestfulWebMethod;



public class UserRestServiceImpl implements UserRestService {

	private final ApplicationStateManager applicationStateManager; 
	
	private final UserFacade userFacade;
       
	private final PropertiesSymbolProvider propertiesSymbolProvider;
	
	private final Marshaller xstreamMarshaller;
	
	private final MailContainerBuilder mailContainerBuilder;
	
	private final AbstractDomainFacade domainFacade;
	
	private static final Logger logger = LoggerFactory.getLogger(UserRestServiceImpl.class);

	public UserRestServiceImpl(final ApplicationStateManager applicationStateManager,
			final UserFacade userFacade,
			final PropertiesSymbolProvider propertiesSymbolProvider,
			final Marshaller xstreamMarshaller,
			final MailContainerBuilder mailContainerBuilder,
			final AbstractDomainFacade domainFacade) {
		super();
		this.applicationStateManager = applicationStateManager;
		this.userFacade = userFacade;
		this.propertiesSymbolProvider = propertiesSymbolProvider;
		this.xstreamMarshaller = xstreamMarshaller;
		this.mailContainerBuilder = mailContainerBuilder;
		this.domainFacade = domainFacade;
	}

	@RestfulWebMethod
	public void createuser(Request request, Response response)
			throws IOException {
		
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);
		
		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if (actor.isGuest()) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}
		
		try {
			if (!domainFacade.userCanCreateGuest(actor)) {
				response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
				return;
			}
		} catch (BusinessException e1) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}
		
		if (!"POST".equals(request.getMethod())) {
			response.sendError(HttpStatus.SC_METHOD_NOT_ALLOWED, "Method not allowed");
			response.setHeader("Allow", "POST");
			return;
		}
		
		if (request.getParameterNames().size()<4) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Not enough parameters");
			return;
		}

		if (!request.getParameterNames().contains("firstName")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter firstName");
			return;

		}
		String firstName =request.getParameter("firstName") ;
		
		if (!request.getParameterNames().contains("lastName")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter lastName");
			return;

		}
		String lastName =request.getParameter("lastName") ;
		
		if (!request.getParameterNames().contains("mail")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter mail");
			return;

		}
		String mail =request.getParameter("mail") ;

		// must validate the mail format
		if (!mail.matches("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Wrong mail type");
			return;
		}

		if (!request.getParameterNames().contains("canUpload")) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter canUpload");
			return;

		}
		String canUpload =request.getParameter("canUpload") ;
		String canCreateGuest =request.getParameter("canCreateGuest") ;
		
		String url=propertiesSymbolProvider.valueForSymbol("linshare.info.url.base");

		boolean uploadGranted = ((canUpload != null) && ("true".equals(canUpload)));
		boolean createGuestGranted = ((canCreateGuest != null) && ("true".equals(canCreateGuest)));
		
        try {
            userFacade.createGuest(mail, firstName, lastName, uploadGranted, createGuestGranted, "", actor);
            logger.info("User " + mail + " successfully created");
        } catch (BusinessException e) {
        	logger.error(e.toString());
        	response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
        	response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't create the user " + e.getMessage());
        	return;
        }
        
        UserVo user = null;
//		try {
			user = userFacade.findUserInDb(mail, actor.getDomainIdentifier());
//		} catch (BusinessException e) {
//        	logger.error(e.toString());
//        	response.setHeader("BusinessError", e.getErrorCode().getCode()+"");
//        	response.sendError(HttpStatus.SC_METHOD_FAILURE, "Couldn't load the user " + e.getMessage());
//        	return;
//		}    
        
        String xml = xstreamMarshaller.toXml(user);
		
		PrintWriter writer = response.getPrintWriter("text/xml");
		response.setStatus(HttpStatus.SC_CREATED);
		
		// Write the location of the new ressource
		response.setHeader("Location", url +"userrestservice/getuser/" + user.getMail());
		writer.append(xml);
		writer.flush();
		writer.close();
        
	}

	
	@RestfulWebMethod
	public void getuser(Request request, Response response, String mail)
			throws IOException {
		
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);
		
		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if ( (actor.isGuest() && (!actor.isUpload()))) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}
		
		// fetch the target		
		UserVo user = null;
//		try {
			user = userFacade.findUserInDb(mail, actor.getDomainIdentifier());
//		} catch (BusinessException e) {
//			response.sendError(HttpStatus.SC_NOT_FOUND, "User not found");
//			return;
//		}
	
		if (user == null) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "User not found");
			return;
		}
		String xml = xstreamMarshaller.toXml(user);
		
		PrintWriter writer = response.getPrintWriter("text/xml");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
	}

	@RestfulWebMethod
	public void autoCompleteUser(Request request, Response response, String mail) throws IOException {
		UserVo actor = applicationStateManager.getIfExists(UserVo.class);
		
		if (actor== null) {
			response.sendError(HttpStatus.SC_UNAUTHORIZED, "You are not authorized to use this service");
			return;
		}
		
		if ( (actor.isGuest() && (!actor.isUpload()))) {
			response.sendError(HttpStatus.SC_FORBIDDEN, "You are not authorized to use this service");
			return;
		}
		
		if (mail == null || mail.length() == 0) {
			response.sendError(HttpStatus.SC_BAD_REQUEST, "Missing parameter mail");
			return;

		}
		mail = URLDecoder.decode(mail, "utf-8");
		String firstName = null;
		String lastName = null;

		StringTokenizer stringTokenizer = new StringTokenizer(mail, " ");
		if (stringTokenizer.hasMoreTokens()) {
			firstName = stringTokenizer.nextToken();
			if (stringTokenizer.hasMoreTokens()) {
				lastName = stringTokenizer.nextToken();
			}
		}

		Set<UserVo> userSet = new HashSet<UserVo>();
        try {
			userSet.addAll(userFacade.searchUser(mail.trim(), null, null, actor));
			userSet.addAll(userFacade.searchUser(null, firstName, lastName, actor));
			userSet.addAll(userFacade.searchUser(null, lastName, firstName, actor));
		} catch (BusinessException e) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "User domain not found");
			return;
		}
		
		
		List<UserVo> listUser =  new ArrayList<UserVo>(userSet);

		if (listUser.size()==0) {
			response.sendError(HttpStatus.SC_NOT_FOUND, "User not found");
			return;
		}
		String xml = xstreamMarshaller.toXml(listUser);
		
		PrintWriter writer = response.getPrintWriter("text/xml");
		response.setStatus(HttpStatus.SC_OK);
		writer.append(xml);
		writer.flush();
		writer.close();
		
	}
	
}
