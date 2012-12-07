package org.linagora.linshare.service.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import junit.framework.TestCase;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.Test;
import org.linagora.linshare.webservice.dto.Document;
import org.linagora.linshare.webservice.dto.DocumentAttachement;
import org.linagora.linshare.webservice.dto.SimpleLongValue;


public class RestWebserviceTest extends TestCase {

	public static final String WEBSERVICE_REST_URL = "http://localhost:8080/linshare/webservice/rest/";
	public static final String USER = "bart.simpson@int1.linshare.dev";
	public static final String SHARE_TO_USER = "maggie.simpson@int1.linshare.dev";
	
	public static final String PASSWORD = "secret";

	/**
	 * REST. test Xop can send --very-- large file
	 */
	/**
	 * @param args
	 * @return 
	 * @throws IOException
	 */
	@Test
	public static String testAddFileXop() throws IOException {

		String address = WEBSERVICE_REST_URL + "/document/xop";

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(address);

		// basic authentication ?
		bean.setUsername(USER);
		bean.setPassword(PASSWORD);

		bean.setProperties(Collections.singletonMap(
				org.apache.cxf.message.Message.MTOM_ENABLED, (Object) "true"));
		WebClient client = bean.createWebClient();
		HTTPConduit conduit = WebClient.getConfig(client).getHttpConduit();
		conduit.getClient().setReceiveTimeout(1000000);
		conduit.getClient().setConnectionTimeout(1000000);
		client.type("multipart/form-data").accept("multipart/form-data");
		DocumentAttachement xop = new DocumentAttachement();

		// send webserviceRest.html with filename fichier.htm and comment toto
		xop.setFilename("fichier.htm");
		xop.setComment("toto");
		InputStream is = RestWebserviceTest.class
				.getResourceAsStream("webserviceRest.html");
		if (is == null)
			throw new FileNotFoundException("webserviceRest.html");

		byte[] data = IOUtils.readBytesFromStream(is);
		xop.setDocument(new DataHandler(new ByteArrayDataSource(data,
				"application/octet-stream")));
		client.accept("application/xml");
		Document res = client.post(xop, Document.class);
		System.out.println("resultat new Document: " + res);

		assertNotNull(res);
		assertEquals("fichier.htm", res.getName());
		
		return res.getId();
	}

	@Test
	public static void testList() throws IOException {

		String address = WEBSERVICE_REST_URL;
		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(address);
		// basic authentication ?
		bean.setUsername(USER);
		bean.setPassword(PASSWORD);
		WebClient client = bean.createWebClient();
		client.path("document/list.xml");
		client.type("text/xml").accept("text/xml");
		Collection<? extends Document> r = client.getCollection(Document.class);
		for (Document document : r) {
			System.out.println(document);
		}
	}
	
	@Test
	public static void testUserMaxFileSize() throws IOException {

		String address = WEBSERVICE_REST_URL;
		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(address);
		// basic authentication ?
		bean.setUsername(USER);
		bean.setPassword(PASSWORD);
		
		//test XML ouput
		
		WebClient client = bean.createWebClient();
		client.path("document/userMaxFileSize.xml");
		client.accept("text/xml");
		SimpleLongValue slv  = client.get(SimpleLongValue.class);
		System.out.println(slv.getValue());
		
		//test Json output
		
		WebClient clientJson = bean.createWebClient();
		clientJson.path("document/userMaxFileSize.json");
		clientJson.accept("application/json");
		String response = clientJson.get(String.class);
		System.out.println(response);
        assertTrue(response.startsWith("{"));
        assertTrue(response.endsWith("}"));
        assertTrue(response.contains("\"value\":"));
	}
	
	@Test
	//http://localhost:8080/linshare/webservice/rest/share/sharedocument/maggie.simpson@int1.linshare.dev/7e6fb8a3-82dd-48c1-a3b8-f0eaaff815fb?securedShare=1"
	public static void testShare() throws IOException {

		String address = WEBSERVICE_REST_URL;
		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(address);
		// basic authentication ?
		bean.setUsername(USER);
		bean.setPassword(PASSWORD);
		
		String uid = testAddFileXop();
		
		WebClient client = bean.createWebClient();
		String mypath = "share/sharedocument/" + SHARE_TO_USER + "/" + uid;
		client.path(mypath);
		client.accept("text/xml");
		client.get();
	}
}
