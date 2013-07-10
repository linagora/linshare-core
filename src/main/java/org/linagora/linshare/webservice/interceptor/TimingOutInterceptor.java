package org.linagora.linshare.webservice.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TimingOutInterceptor extends AbstractPhaseInterceptor<Message>{

	private static final Logger logger = LoggerFactory.getLogger(TimingOutInterceptor.class);
	
	public TimingOutInterceptor() {
		super(Phase.SEND_ENDING);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		Long startTime = (Long) message.getExchange().get("org.linagora.linshare.webservice.interceptor.start_time");
		long elapsed = System.currentTimeMillis() - startTime;
		String url = (String) message.getExchange().get(Message.REQUEST_URI);
		String method = (String) message.getExchange().get(Message.HTTP_REQUEST_METHOD);
		logger.info(String.format("%s:%s : Request time: %d ms", method, url, elapsed));
	}
}
