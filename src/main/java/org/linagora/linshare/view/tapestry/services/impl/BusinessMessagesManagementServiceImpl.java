/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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
package org.linagora.linshare.view.tapestry.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.objects.BusinessInformativeContentBundle;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;

/** This service manages the business errors.
 */
public class BusinessMessagesManagementServiceImpl implements BusinessMessagesManagementService {

    private static final String ERROR_CODE_PREFIX = "error.code.";
    private static final String BUSINESS_MESSAGE_PREFIX = "business.message.";

    
    private final ApplicationStateManager stateManager;
    
    
    //we can not do that because BusinessMessagesManagementServiceImpl is a singleton
    // and this field is injected only once for the first user...
    //@ApplicationState
    //private BusinessInformativeContentBundle businessInformativeContentBundle;
    
    
    public BusinessMessagesManagementServiceImpl(ApplicationStateManager stateManager){
    	this.stateManager = stateManager;
    }
    

    public void notify(BusinessException exception) {
        getBusinessInformativeContentBundle().getBusinessExceptions().add(exception);
    }

    public void notify(BusinessUserMessage businessUserMessage) {
        getBusinessInformativeContentBundle().getBusinessUserMessages().add(businessUserMessage);
    }

    public List<String> getBusinessMessages(Messages messages) {
    	return getMessages(messages, true);
    }

    public List<String> getRawBusinessMessages(Messages messages) {
    	return getMessages(messages, false);
    }
    
    private List<String> getMessages(Messages messages, boolean severity) {
        List<String> errorMessages = new ArrayList<String>();

        // First we process business exceptions :
        for (BusinessException businessException : getBusinessInformativeContentBundle().getBusinessExceptions()) {
            String key = ERROR_CODE_PREFIX + businessException.getErrorCode().toString().toLowerCase();
            String message = messages.get(key);
            message = formatMessage(message, businessException.getExtras());
            errorMessages.add(severity ? addSeverityStyle(message, MessageSeverity.ERROR) : message);
        }
        // Now we have to manage business user messages :
        for (BusinessUserMessage businessUserMessage : getBusinessInformativeContentBundle().getBusinessUserMessages()) {
            String key = BUSINESS_MESSAGE_PREFIX + businessUserMessage.getBusinessUserMessageType().toString().toLowerCase();
            String message = messages.get(key);
            message = formatMessage(message, businessUserMessage.getExtras());
            errorMessages.add(severity ? addSeverityStyle(message, businessUserMessage.getSeverity()) : message);
        }
        return errorMessages;
    }

    public void reset() {
        getBusinessInformativeContentBundle().setBusinessExceptions(new ArrayList<BusinessException>());
        getBusinessInformativeContentBundle().setBusinessUserMessages(new ArrayList<BusinessUserMessage>());
    }

    public boolean isListEmpty() {
        return getBusinessInformativeContentBundle().getBusinessExceptions().isEmpty()
            && getBusinessInformativeContentBundle().getBusinessUserMessages().isEmpty();
    }

    private BusinessInformativeContentBundle getBusinessInformativeContentBundle() {

        
    	BusinessInformativeContentBundle businessInformativeContentBundle = this.stateManager.get(BusinessInformativeContentBundle.class);
    	
    	
    	if (businessInformativeContentBundle == null) {
            businessInformativeContentBundle = new BusinessInformativeContentBundle();
        }
        
        return businessInformativeContentBundle;
    }

    private String formatMessage(String message, List<String> args) {
       
    	if (args==null) return message;
    	
    	for (String arg : args) {
            message = message.replaceFirst("%s", arg);
        }
        return message;
    }

    private String addSeverityStyle(String message, MessageSeverity severity) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<span class=\"");
        switch (severity) {
            case INFO : buffer.append("message-info"); break;
            case WARNING : buffer.append("message-warn"); break;
            case ERROR : buffer.append("message-error"); break;
        }
        buffer.append("\"/>");
        buffer.append(message);
        buffer.append("</span>");
        return buffer.toString();
    }
}
