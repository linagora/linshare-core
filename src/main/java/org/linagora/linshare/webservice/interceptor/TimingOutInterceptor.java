/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.webservice.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger logger = LoggerFactory
			.getLogger(TimingOutInterceptor.class);

	public TimingOutInterceptor() {
		super(Phase.SEND_ENDING);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		Long startTime = (Long) message.getExchange().get(
				"org.linagora.linshare.webservice.interceptor.start_time");
		long elapsed = System.currentTimeMillis() - startTime;
		String url = (String) message.getExchange().get(Message.REQUEST_URI);
		Integer response_code = (Integer) message.get(Message.RESPONSE_CODE);
		String method = (String) message.getExchange().get(Message.HTTP_REQUEST_METHOD);
		// traces for flow uploader are skipped to avoid flooding.
		if (url.endsWith("webservice/rest/user/v2/flow.json")) {
			logger.trace(String.format("%s:%s:%s: Request time: %d ms", method, response_code, url, elapsed));
		} else if (url.endsWith("webservice/rest/user/v4/flow.json")) {
			logger.trace(String.format("%s:%s:%s: Request time: %d ms", method, response_code, url, elapsed));
		} else {
			logger.debug(String.format("%s:%s:%s: Request time: %d ms", method, response_code, url, elapsed));
		}
	}
}
