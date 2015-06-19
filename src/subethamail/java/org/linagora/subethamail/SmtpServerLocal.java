/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.subethamail;

import org.subethamail.smtp.auth.LoginFailedException;
import org.subethamail.smtp.auth.PlainAuthenticationHandlerFactory;
import org.subethamail.smtp.auth.UsernamePasswordValidator;
import org.subethamail.smtp.server.SMTPServer;


public class SmtpServerLocal {
	
	private int port;
	private String user;
	private String password;
	private String outputDir;
	
	public void start() {
		
            MyMessageHandlerFactory myFactory = new MyMessageHandlerFactory(outputDir) ;
            SMTPServer smtpServer = new SMTPServer(myFactory);
            smtpServer.setPort(port);
            
            PlainAuthenticationHandlerFactory p = new PlainAuthenticationHandlerFactory(new UsernamePasswordValidator(){@Override
            public void login(String arg0, String arg1)
            		throws LoginFailedException {
            	if (!(arg0.equalsIgnoreCase(user) && arg1.equalsIgnoreCase(password))){
            		throw new LoginFailedException("SubEthaMail: unable to authenticate the user");
            	}
            	
            }});
            
            smtpServer.setAuthenticationHandlerFactory(p);
            
            smtpServer.start();
            System.out.println("SubEthaMail (local Smtp Server) is now started .......");
        }

	public void setPort(int port) {
		this.port = port;
	}
	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
}

