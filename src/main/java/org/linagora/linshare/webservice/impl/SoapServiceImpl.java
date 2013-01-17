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

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.soap.MTOM;

import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.WebServiceDocumentFacade;
import org.linagora.linshare.core.facade.WebServiceShareFacade;
import org.linagora.linshare.webservice.SoapService;
import org.linagora.linshare.webservice.dto.Document;
import org.linagora.linshare.webservice.dto.DocumentAttachement;
import org.linagora.linshare.webservice.dto.SimpleLongValue;

@WebService(serviceName = "SoapWebService", endpointInterface = "org.linagora.linshare.webservice.SoapService",
	targetNamespace = WebserviceBase.NAME_SPACE_NS, portName = "SoapServicePort")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,parameterStyle = ParameterStyle.WRAPPED ,use = SOAPBinding.Use.LITERAL)
@MTOM
public class SoapServiceImpl extends WebserviceBase implements
		SoapService {


	private final WebServiceDocumentFacade webServiceDocumentFacade;

	private final WebServiceShareFacade webServiceShareFacade;
	
	
	
	public SoapServiceImpl(
			final WebServiceDocumentFacade webServiceDocumentFacade, final WebServiceShareFacade webServiceShareFacade) {
		this.webServiceDocumentFacade = webServiceDocumentFacade;
		this.webServiceShareFacade = webServiceShareFacade;
	}

	
	
	// Documents
	
	/**
	 * get the files of the user
	 * @throws BusinessException 
	 */
	@WebMethod(operationName = "getDocuments")
	// **soap
	@Override
	public List<Document> getDocuments() throws BusinessException {
		webServiceDocumentFacade.checkAuthentication();
		return webServiceDocumentFacade.getDocuments();
	}

	/**
	 * here we use XOP method for large file upload
	 * 
	 * @param doca
	 * @throws BusinessException 
	 */

	@Oneway
	@WebMethod(operationName = "addDocumentXop")
	// **soap
	@Override
	public Document addDocumentXop(DocumentAttachement doca) throws BusinessException {
		webServiceDocumentFacade.checkAuthentication();
		return webServiceDocumentFacade.addDocumentXop(doca);
	}

	@WebMethod(operationName = "getUserMaxFileSize")
	// **soap
	@Override
	public SimpleLongValue getUserMaxFileSize() throws BusinessException {
		webServiceDocumentFacade.checkAuthentication();
		return new SimpleLongValue(webServiceDocumentFacade.getUserMaxFileSize());
	}

	@WebMethod(operationName = "getAvailableSize")
	// **soap
	@Override
	public SimpleLongValue getAvailableSize() throws BusinessException {
		webServiceDocumentFacade.checkAuthentication();
		return new SimpleLongValue(webServiceDocumentFacade.getAvailableSize());
	}
	
	
	// Shares
	@Override
	public void sharedocument(String targetMail, String uuid,int securedShare) throws BusinessException {
		User actor = webServiceShareFacade.checkAuthentication(); //raise exception
		
		if ((actor instanceof Guest  && !actor.getCanUpload())) {
			throw new BusinessException(BusinessErrorCode.WEBSERVICE_UNAUTHORIZED, "You are not authorized to use this service");
		}
		
		webServiceShareFacade.sharedocument(targetMail, uuid, securedShare);
	}
	
	
	
	
	// PluginManagment
	@WebMethod(operationName = "getInformation")
	// **soap
	@Override
	public String getInformation() throws BusinessException {
		return "This API is still in developpement";
	}
}
