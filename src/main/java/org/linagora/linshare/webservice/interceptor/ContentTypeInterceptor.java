/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
