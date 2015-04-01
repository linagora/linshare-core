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
package org.linagora.linshare.webservice.interceptor;

import javax.xml.namespace.QName;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.linagora.linshare.core.exception.BusinessException;

@NoJSR250Annotations
public class SoapExceptionInterceptor extends AbstractSoapInterceptor {

	public static final String NAME_SPACE_NS = "http://org/linagora/linshare/webservice/";

	public SoapExceptionInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		Fault fault = (Fault) message.getContent(Exception.class);
		Throwable ex = fault.getCause();

		if (ex instanceof BusinessException) {
			BusinessException e = (BusinessException) ex;
			generateSoapFault(fault, e);
		}
	}

	private void generateSoapFault(Fault fault, BusinessException e) {
		fault.setFaultCode(createQName(e.getErrorCode().getCode()));
		fault.setMessage(e.getMessage());

		switch (e.getErrorCode()) {
		case WEBSERVICE_FAULT:
			fault.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			break;
		case WEBSERVICE_FORBIDDEN:
			fault.setStatusCode(HttpStatus.SC_FORBIDDEN);
			break;
		case FORBIDDEN:
			fault.setStatusCode(HttpStatus.SC_FORBIDDEN);
			break;
		case USER_NOT_FOUND:
			fault.setStatusCode(HttpStatus.SC_NOT_FOUND);
			break;
		case WEBSERVICE_NOT_FOUND:
			fault.setStatusCode(HttpStatus.SC_NOT_FOUND);
			break;
		default:
			fault.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private static QName createQName(int errorCode) {
		return new QName(NAME_SPACE_NS, String.valueOf(errorCode));
	}
}
