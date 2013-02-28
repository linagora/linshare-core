package org.linagora.linshare.webservice;

import javax.jws.WebService;

import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.webservice.dto.DocumentAttachement;
import org.linagora.linshare.webservice.dto.DocumentDto;

/**
 * This interface was create to support MTOM (XOP) upload. When MTOM is activated, all SOAP messages are built using multipart format. 
 * @author fmartin
 *
 */
@WebService
public interface MTOMUploadSoapService {

	public DocumentDto addDocumentXop(DocumentAttachement doca) throws BusinessException;
	
	public String getInformation() throws BusinessException;
}
