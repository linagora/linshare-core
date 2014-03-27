/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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
package org.linagora.linshare.service.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.xml.ws.BindingProvider;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;
import org.linagora.linshare.webservice.test.soap.BusinessException_Exception;
import org.linagora.linshare.webservice.test.soap.Document;
import org.linagora.linshare.webservice.test.soap.DocumentAttachement;
import org.linagora.linshare.webservice.test.soap.SimpleLongValue;
import org.linagora.linshare.webservice.test.soap.SoapService;
import org.linagora.linshare.webservice.test.soap.SoapWebService;

@Ignore
public class SoapWebserviceTest extends TestCase {

	public static final String WEBSERVICE_SOAP_URL = "http://localhost:8080/linshare/webservice/soap";
	public static final String WEBSERVICE_SOAP_URL_DOCUMENT = WEBSERVICE_SOAP_URL;
	public static final String WEBSERVICE_SOAP_URL_SHARE = WEBSERVICE_SOAP_URL;
	
	public static final String USER = "bart.simpson@int1.linshare.dev";
	public static final String PASSWORD = "secret";
	
	public static final String SHARE_TO_USER = "maggie.simpson@int1.linshare.dev";


	@Test
	public void  testAddDocument() throws IOException, BusinessException_Exception{
			addDocument();
	}
	
	@Test
	public void testUserMaxFileSize() throws IOException, BusinessException_Exception {

		SoapService proxy = getProxyToSoapWebService();
		SimpleLongValue slv = proxy.getUserMaxFileSize();
		assertNotNull(slv);
	}
	
	@Test
	public void testList() throws IOException, BusinessException_Exception {

		SoapService proxy = getProxyToSoapWebService();
		
		List<Document> list = proxy.getDocuments();
		assertNotNull(list);
		if(list.size()>0){
			Document doc0 = list.get(0);
			assertNotNull(doc0.getUuid()); //mandatory field
		}
	}
	@Test
	public void testShareDoc() throws IOException, BusinessException_Exception {
		
		//1) **** add doc
		Document newdoc = addDocument();
		
		//2) **** check doc in list
		assertTrue(checkDocumentInList(newdoc));
		
		//now share it !
		SoapService proxy   = getProxyToSoapWebService();
		proxy.sharedocument(SHARE_TO_USER, newdoc.getUuid(), 0);
		
		String fakeUid  = "00000000-0000-0000-0000-000000000000";
		try {
			proxy.sharedocument(SHARE_TO_USER, fakeUid, 0);
		} catch (BusinessException_Exception e) {
			assertEquals("Can not find document entry with uuid : 00000000-0000-0000-0000-000000000000", e.getMessage());
		}
	}
	
	// ****************   utility methods
	
	
	/**
	 * get a proxy to soap/document
	 * @return
	 */
	private SoapService getProxyToSoapWebService() {
		SoapWebService soap = new SoapWebService();
		SoapService proxy = soap.getSoapServicePort();
				
		Map<String, Object> context = ((BindingProvider)proxy).getRequestContext();

		context.put(BindingProvider.USERNAME_PROPERTY, USER);
		context.put(BindingProvider.PASSWORD_PROPERTY, PASSWORD);
	    context.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, WEBSERVICE_SOAP_URL_DOCUMENT);
	    
	    return proxy;
	}
	
	
	private Document addDocument() throws FileNotFoundException, BusinessException_Exception{
		
		SoapService proxy = getProxyToSoapWebService();
				
	    Document result = null;
	    
	    try {
			DocumentAttachement da = new DocumentAttachement();
			da.setFilename("soapDoc.txt");
			da.setComment("test soap add document");
			
			URL url = SoapWebserviceTest.class.getResource("/webserviceRest.html");
			if (url == null)
				throw new FileNotFoundException("/webserviceRest.html");

			da.setDocument(new DataHandler(url));
			result = proxy.addDocumentXop(da);
			
			assertNotNull(result);
			assertEquals("soapDoc.txt", result.getName());
				
		} catch (BusinessException_Exception e) {
			throw e;
		}
		
		 return result;
	}
	
	private boolean checkDocumentInList(Document newdoc) throws IOException, BusinessException_Exception {
		
		SoapService proxy = getProxyToSoapWebService();
		
		List<Document> list = proxy.getDocuments();
		
		boolean found = false;
		
		for (Document currentDoc : list) {
			if(currentDoc.getUuid().equals(newdoc.getUuid())){
				assertEquals(currentDoc.getCreation(), newdoc.getCreation());
				assertEquals(currentDoc.getName(), newdoc.getName());
				found = true;
				break;
			}
		}
		return found;
	}
	
	

	
}
