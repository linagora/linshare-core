package org.linagora.linshare.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.subethamail.smtp.MessageContext;
import org.subethamail.smtp.MessageHandler;
import org.subethamail.smtp.MessageHandlerFactory;
import org.subethamail.smtp.RejectException;

public class LinShareMessageHandler implements MessageHandlerFactory {


    public MessageHandler create(MessageContext ctx) {
        return new Handler(ctx);
    }

    class Handler implements MessageHandler {
        MessageContext ctx;

        public Handler(MessageContext ctx) {
                this.ctx = ctx;
        }
        
        public void from(String from) throws RejectException {
                System.out.println("FROM:"+from);
        }

        public void recipient(String recipient) throws RejectException {
                System.out.println("RECIPIENT:"+recipient);
        }

        public void data(InputStream data) throws IOException {
                System.out.println("MAIL DATA");
                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
                System.out.println(this.convertStreamToString(data));
                System.out.println("= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =");
        }

        public void done() {
                System.out.println("Finished");
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


		public boolean auth(String clientInput, StringBuffer response)
				throws RejectException {
			return false;
		}


		public List<String> getAuthenticationMechanisms() {
			return new ArrayList<String>();
		}

		public void resetState() {
			
		}

    }

}
