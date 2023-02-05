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

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingInInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger logger = LoggerFactory.getLogger(TimingInInterceptor.class);

	public TimingInInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		long startTime = System.currentTimeMillis();
		String url = (String) message.get(Message.REQUEST_URI);
		String method = (String) message.get(Message.HTTP_REQUEST_METHOD);
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s:%s", method, url + " ..."));
		}
		message.getExchange().put("org.linagora.linshare.webservice.interceptor.start_time", startTime);
		message.getExchange().put(Message.REQUEST_URI, url);
		message.getExchange().put(Message.HTTP_REQUEST_METHOD, method);
	}
}
