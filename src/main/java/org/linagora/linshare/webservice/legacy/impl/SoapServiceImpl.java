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

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.ShareDto;
import org.linagora.linshare.core.facade.webservice.common.dto.SimpleLongValue;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupDto;
import org.linagora.linshare.core.facade.webservice.common.dto.WorkGroupMemberDto;
import org.linagora.linshare.core.facade.webservice.common.dto.UserDto;
import org.linagora.linshare.core.facade.webservice.user.DocumentFacade;
import org.linagora.linshare.core.facade.webservice.user.ShareFacade;
import org.linagora.linshare.core.facade.webservice.user.WorkGroupFacade;
import org.linagora.linshare.core.facade.webservice.user.UserFacade;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.linagora.linshare.webservice.WebserviceBase;
import org.linagora.linshare.webservice.legacy.SoapService;

@WebService(serviceName = "SoapWebService",
			endpointInterface = "org.linagora.linshare.webservice.SoapService",
			targetNamespace = WebserviceBase.NAME_SPACE_NS,
			portName = "SoapServicePort")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT,
			 parameterStyle = ParameterStyle.WRAPPED,
			 use = SOAPBinding.Use.LITERAL)
public class SoapServiceImpl extends WebserviceBase implements SoapService {

	private final DocumentFacade webServiceDocumentFacade;

	private final ShareFacade webServiceShareFacade;

	private final WorkGroupFacade webServiceThreadFacade;

	private final UserFacade webServiceUserFacade;

	public SoapServiceImpl(
			final DocumentFacade webServiceDocumentFacade,
			final ShareFacade webServiceShareFacade,
			WorkGroupFacade webServiceThreadFacade,
			UserFacade webServiceUserFacade) {
		this.webServiceDocumentFacade = webServiceDocumentFacade;
		this.webServiceShareFacade = webServiceShareFacade;
		this.webServiceThreadFacade = webServiceThreadFacade;
		this.webServiceUserFacade = webServiceUserFacade;
	}

	// Documents

	@WebMethod(operationName = "getDocuments")
	// **soap
	@Override
	public List<DocumentDto> getDocuments() throws BusinessException {
		return webServiceDocumentFacade.findAll();
	}

	@WebMethod(operationName = "getUserMaxFileSize")
	// **soap
	@Override
	public SimpleLongValue getUserMaxFileSize() throws BusinessException {
		return null;
	}

	@WebMethod(operationName = "getAvailableSize")
	// **soap
	@Override
	public SimpleLongValue getAvailableSize() throws BusinessException {
		return null;
	}

	// Shares
	@Override
	public void sharedocument(String targetMail, String uuid, int securedShare)
			throws BusinessException {
		webServiceShareFacade.sharedocument(targetMail, uuid, securedShare);
	}

	@WebMethod(operationName = "getReceivedShares")
	// **soap
	@Override
	public List<ShareDto> getReceivedShares() throws BusinessException {
		return webServiceShareFacade.getReceivedShares();
	}

	// PluginManagment
	@WebMethod(operationName = "getInformation")
	// **soap
	@Override
	public String getInformation() throws BusinessException {
		return "This API is still in developpement";
	}

	// Threads

	@Override
	public List<WorkGroupDto> getAllMyThread() throws BusinessException {
		return webServiceThreadFacade.findAll();
	}

	@Override
	public void addMember(WorkGroupMemberDto member) throws BusinessException {
		webServiceThreadFacade.addMember(member.getThreadUuid(), member.getUserDomainId(),
				member.getUserMail(), member.isReadonly());
	}

	// Users
	@Override
	public List<UserDto> getUsers() throws BusinessException {
		return webServiceUserFacade.findAll();
	}
}
