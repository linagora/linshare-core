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

