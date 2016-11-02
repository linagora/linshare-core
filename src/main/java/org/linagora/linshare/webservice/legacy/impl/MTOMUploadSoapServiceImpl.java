/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.webservice.legacy.impl;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.xml.ws.soap.MTOM;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.DocumentAttachement;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.legacy.MTOMUploadSoapService;

/**
 * All CXF Outbound Message will be using multipart format.
 * 
 * @author fmartin
 * 
 */
@WebService(serviceName = "MTOMUploadSoapService",
			endpointInterface = "org.linagora.linshare.webservice.MTOMUploadSoapService",
			targetNamespace = WebserviceBase.NAME_SPACE_NS,
			portName = "MTOMUploadSoapServicePort")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,
			 parameterStyle = ParameterStyle.WRAPPED,
			 use = SOAPBinding.Use.LITERAL)
@MTOM
public class MTOMUploadSoapServiceImpl implements MTOMUploadSoapService {

	private final DocumentFacade webServiceDocumentFacade;

	public MTOMUploadSoapServiceImpl(
			DocumentFacade webServiceDocumentFacade) {
		super();
		this.webServiceDocumentFacade = webServiceDocumentFacade;
	}

	/**
	 * here we use XOP method for large file upload
	 * 
	 */
	@Oneway
	@WebMethod(operationName = "addDocumentXop")
	// **soap
	@Override
	public DocumentDto addDocumentXop(DocumentAttachement doca)
			throws BusinessException {
		return webServiceDocumentFacade.addDocumentXop(doca);
	}

	@WebMethod(operationName = "getInformation")
	// **soap
	@Override
	public String getInformation() throws BusinessException {
		return "This API is still in developpement";
	}
}
