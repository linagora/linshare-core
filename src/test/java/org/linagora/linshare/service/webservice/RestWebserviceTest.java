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
package org.linagora.linshare.service.webservice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.linagora.linshare.core.facade.webservice.common.dto.DocumentAttachement;
import org.linagora.linshare.core.facade.webservice.common.dto.SimpleLongValue;
import org.linagora.linshare.core.facade.webservice.user.dto.DocumentDto;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Disabled
public class RestWebserviceTest {

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
				.getResourceAsStream("/webserviceRest.html");
		if (is == null)
			throw new FileNotFoundException("/webserviceRest.html");

		byte[] data = IOUtils.readBytesFromStream(is);
		xop.setDocument(new DataHandler(new ByteArrayDataSource(data,
				"application/octet-stream")));
		client.accept("application/xml");
		DocumentDto res = client.post(xop, DocumentDto.class);
		System.out.println("resultat new Document: " + res);

		Assertions.assertNotNull(res);
		Assertions.assertEquals("fichier.htm", res.getName());
		
		return res.getUuid();
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
		Collection<? extends DocumentDto> r = client.getCollection(DocumentDto.class);
		for (DocumentDto document : r) {
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
        Assertions.assertTrue(response.startsWith("{"));
        Assertions.assertTrue(response.endsWith("}"));
        Assertions.assertTrue(response.contains("\"value\":"));
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
