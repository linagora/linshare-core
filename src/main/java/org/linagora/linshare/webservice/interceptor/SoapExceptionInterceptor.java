package org.linagora.linshare.webservice.interceptor;

import javax.xml.namespace.QName;

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
 
	public void handleMessage(SoapMessage message) throws Fault {
		Fault fault = (Fault) message.getContent(Exception.class);
		Throwable ex = fault.getCause();
		if (ex instanceof BusinessException) {
			BusinessException e = (BusinessException) ex;
			generateSoapFault(fault, e);
		} 
	}
 
	private void generateSoapFault(Fault fault, BusinessException e) {
		
		//we set the BusinessException code in the fault
		fault.setFaultCode(createQName(e.getErrorCode().getCode()));
		//we set the BusinessException
		fault.setMessage(e.getMessage());
	}
 
	private static QName createQName(int errorCode) {
		return new QName(NAME_SPACE_NS, String.valueOf(errorCode));
	}
}
