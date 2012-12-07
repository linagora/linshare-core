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

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.webservice.ShareSoapService;


@WebService(serviceName = "ShareSoapWebService", endpointInterface = 
"org.linagora.linshare.webservice.ShareSoapService", targetNamespace= WebserviceBase.NAME_SPACE_NS, 
portName="ShareSoapServicePort")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,parameterStyle = ParameterStyle.WRAPPED ,use = SOAPBinding.Use.LITERAL)

public class ShareSoapServiceImpl implements ShareSoapService {

	
	private final WebServiceShareFacade webServiceShareFacade;
	
	
	public ShareSoapServiceImpl(final WebServiceShareFacade facade){
		this.webServiceShareFacade = facade;
	}
	
	@Override
	public void sharedocument(String targetMail, String uuid,int securedShare) throws BusinessException {
		User actor = webServiceShareFacade.checkAuthentication(); //raise exception
		
		if ((actor instanceof Guest  && !actor.getCanUpload())) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		
		webServiceShareFacade.sharedocument(targetMail, uuid, securedShare);
	}
	
	
	
	
	

}
