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

