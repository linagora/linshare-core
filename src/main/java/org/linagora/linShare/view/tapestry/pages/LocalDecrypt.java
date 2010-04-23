/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.pages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.linagora.linShare.core.domain.objects.FileInfo;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.objects.CustomStreamResponse;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this page is a JWS launcher for the aes local decrypt application
 * the application is standalone application deployable as an applet
 * this page takes a jnlp template and set the codebase with key in linshare.properties
 * @author slevesque
 *
 */
public class LocalDecrypt {

	private static final Logger logger = LoggerFactory.getLogger(LocalDecrypt.class);
	
	/***************************************************************************
	 * Properties
	 **************************************************************************/


	/***************************************************************************
	 * Service injection
	 **************************************************************************/

	@Inject @Symbol("javawebstart.decrypt.url.suffixcodebase")
	private String suffixcodebase;
	
	
	@Inject @Symbol("linshare.info.url.base")
	private String linshareInfoUrlBase;
	
	
	@Inject
	@Path("context:templates/jws/localDecrypt.jnlp")
	private Asset jwsTemplate;
	
	@Inject
	private Templating templating;
	
	
	public CustomStreamResponse onActivate() {
		
		try {
			String tplcontent = templating.readFullyTemplateContent(jwsTemplate.getResource().openStream());
			
			Map<String,String> templateParams=new HashMap<String, String>();
			
			//result codebase for JNLP is an url like http://localhost:8080/linshare/applet to download jwsDecrypt.jar
			StringBuffer jwsUrlToPut = new StringBuffer(linshareInfoUrlBase);
			if(!linshareInfoUrlBase.endsWith("/")) jwsUrlToPut.append("/");
			jwsUrlToPut.append(suffixcodebase); //application jws directory: applet in this case
			if(suffixcodebase.endsWith("/")) jwsUrlToPut.deleteCharAt(jwsUrlToPut.length()-1);
			
			templateParams.put("${javawebstart.decrypt.url.codebase}", jwsUrlToPut.toString());
			String jnlp = templating.getMessage(tplcontent, templateParams);
			
			byte[] send = jnlp.getBytes();
			long size = send.length;
			ByteArrayInputStream bi = new ByteArrayInputStream(send);
			
			return new CustomStreamResponse(new FileInfo("","localDecrypt.jnlp","",size,"application/x-java-jnlp-file"),bi);
			
		} catch (IOException e) {
			logger.error("Bad jws template", e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC,"Bad jws template",e);
		}
	}
	
}
