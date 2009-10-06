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
package org.linagora.linShare.view.tapestry.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.services.Templating;

public class MailTemplating implements Templating {

	
	public String getMessage(InputStream template, Map<String,String> parameters) {
		String templateContent = readFullyTemplateContent(template);
		return getMessage(templateContent,parameters);
	}
	
	
	public String getMessage(String templateContent, Map<String,String> parameters) {
		
		Set<Map.Entry<String,String>> params=parameters.entrySet();
		for(Map.Entry<String,String> entry:params){
			templateContent=StringUtils.replace(templateContent, entry.getKey(), entry.getValue());
		}
        
		return templateContent;
	}
	
	
	public String readFullyTemplateContent(InputStream template) {
		
		String content="";
		StringWriter writer=new StringWriter();

		try {
	        InputStreamReader streamReader=new InputStreamReader(template);
            BufferedReader buffer=new BufferedReader(streamReader);
            String line="";
            while ( null!=(line=buffer.readLine())){
                 writer.write(line+"\n");
            }
            content=writer.toString();
        } catch (IOException e) { 
        	throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"The template is not valid",e);
        } catch( Exception ex){
        	throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"The template is not valid",ex);
        }
        
		return content;
	 }
}
