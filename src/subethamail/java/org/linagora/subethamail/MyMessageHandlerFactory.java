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
        	logger.info("mail file is :" + out);
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