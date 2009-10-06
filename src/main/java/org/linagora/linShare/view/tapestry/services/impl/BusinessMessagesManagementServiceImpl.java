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

import java.util.ArrayList;
import java.util.List;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.ioc.Messages;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.objects.BusinessInformativeContentBundle;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linShare.view.tapestry.objects.MessageSeverity;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;

/** This service manages the business errors.
 */
public class BusinessMessagesManagementServiceImpl implements BusinessMessagesManagementService {

    private static final String ERROR_CODE_PREFIX = "error.code.";
    private static final String BUSINESS_MESSAGE_PREFIX = "business.message.";

    @ApplicationState
    private BusinessInformativeContentBundle businessInformativeContentBundle;

    public void notify(BusinessException exception) {
        getBusinessInformativeContentBundle().getBusinessExceptions().add(exception);
    }

    public void notify(BusinessUserMessage businessUserMessage) {
        getBusinessInformativeContentBundle().getBusinessUserMessages().add(businessUserMessage);
    }

    public List<String> getBusinessMessages(Messages messages) {

        List<String> errorMessages = new ArrayList<String>();

        // First we process business exceptions :
        for (BusinessException businessException : getBusinessInformativeContentBundle().getBusinessExceptions()) {
            String key = ERROR_CODE_PREFIX + businessException.getErrorCode().toString().toLowerCase();
            String message = messages.get(key);
            message = formatMessage(message, businessException.getExtras());
            errorMessages.add(addSeverityStyle(message, MessageSeverity.ERROR));
        }

        // Now we have to manage business user messages :
        for (BusinessUserMessage businessUserMessage : getBusinessInformativeContentBundle().getBusinessUserMessages()) {
            String key = BUSINESS_MESSAGE_PREFIX + businessUserMessage.getBusinessUserMessageType().toString().toLowerCase();
            String message = messages.get(key);
            message = formatMessage(message, businessUserMessage.getExtras());
            errorMessages.add(addSeverityStyle(message, businessUserMessage.getSeverity()));
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
