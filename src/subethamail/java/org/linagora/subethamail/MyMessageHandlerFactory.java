package org.linagora.subethamail;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;


public class MyMessageHandlerFactory implements MessageHandlerFactory {


	private static final Logger logger = LoggerFactory.getLogger(MyMessageHandlerFactory.class); 
	
	
	public MessageHandler create(MessageContext ctx) {
    	return new ConsoleHandler(ctx);
    }
	
	
	private String dirout;
	
	public MyMessageHandlerFactory(String outputDir){
		
		dirout = outputDir;
		
		if( ! new File(dirout).exists()){
			new File(dirout).mkdirs();
		}
	}

    class ConsoleHandler implements MessageHandler {
        MessageContext ctx;

        public ConsoleHandler(MessageContext ctx) {
                this.ctx = ctx;
        }
        
        public void from(String from) throws RejectException {
                logger.info("mail FROM:"+from);
        }

        public void recipient(String recipient) throws RejectException {
                logger.info("mail RECIPIENT:"+recipient);
        }

        public void data(InputStream data) throws IOException {
                
        	Date now = new Date();
        	File out = new File(dirout,"message_"+now.getTime()+".eml");
        	FileOutputStream fo = new FileOutputStream(out);
        	fo.write(this.convertStreamToString(data).getBytes());
        	logger.info("mail file is:"+out);
        	fo.flush();
        	fo.close();
        }

        public void done() {
        }

        public String convertStreamToString(InputStream is) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                
                String line = null;
                try {
                        while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
                return sb.toString();
        }

    }
    
}