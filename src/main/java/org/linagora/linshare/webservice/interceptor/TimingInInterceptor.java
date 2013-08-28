package org.linagora.linshare.webservice.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimingInInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger logger = LoggerFactory
			.getLogger(TimingInInterceptor.class);

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
		message.getExchange().put(
				"org.linagora.linshare.webservice.interceptor.start_time",
				startTime);
		message.getExchange().put(Message.REQUEST_URI, url);
		message.getExchange().put(Message.HTTP_REQUEST_METHOD, method);
	}
}
