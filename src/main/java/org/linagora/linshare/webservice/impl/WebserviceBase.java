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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpStatus;
import org.linagora.linshare.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * common utility methods for webservice implementation (rest, soap)
 */
public class WebserviceBase {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(WebserviceBase.class);

	// REST

	protected WebApplicationException giveRestException(int httpErrorCode, String message) {
		return giveRestException(httpErrorCode, message, null);
	}

	protected WebApplicationException giveRestException(int httpErrorCode, String message, Throwable cause) {
		if (cause == null)
			return new WebApplicationException(Response.status(httpErrorCode).entity(message).build());
		else
			return new WebApplicationException(cause, Response.status(httpErrorCode).entity(message).build());
	}

	protected WebApplicationException analyseFaultREST(BusinessException e) {

		// TODO locale in WebApplicationException ?
		WebApplicationException w;

		// BusinessException have a look to BusinessErrorCode
		switch (e.getErrorCode()) {
		case WEBSERVICE_FAULT:
			w = giveRestException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), e);
			break;
		case WEBSERVICE_UNAUTHORIZED:
			w = giveRestException(HttpStatus.SC_FORBIDDEN, e.getMessage());
			break;
		case WEBSERVICE_NOT_FOUND:
			w = giveRestException(HttpStatus.SC_NOT_FOUND, e.getMessage());
			break;
		default:
			w = giveRestException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage(), e);
		}

		return w;
	}

	// SOAP

	public static final String NAME_SPACE_NS = "http://org/linagora/linshare/webservice/";

}
