/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2020-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2021. Contribute to
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.ws.rs.core.MediaType;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.swagger.v3.oas.models.PathItem.HttpMethod;

public class ContentTypeInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger logger = LoggerFactory.getLogger(ContentTypeInterceptor.class);

	private static final List<HttpMethod> httpMethods = Lists.newArrayList(HttpMethod.POST, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.PUT);

	public ContentTypeInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleMessage(Message message) throws Fault {
		if (Objects.isNull(message.get(Message.CONTENT_TYPE))
				&& httpMethods.contains(HttpMethod.valueOf((String) message.get(Message.HTTP_REQUEST_METHOD)))) {
			message.put(Message.CONTENT_TYPE, MediaType.APPLICATION_JSON);
			Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
			headers.put(Message.CONTENT_TYPE, Collections.singletonList(message.get(Message.CONTENT_TYPE)));
			logger.info("The content type is null and its value is overrided by: {}", message.get(Message.CONTENT_TYPE));
		}
	}
}
