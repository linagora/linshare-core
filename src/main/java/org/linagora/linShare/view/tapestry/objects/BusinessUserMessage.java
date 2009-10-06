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
package org.linagora.linShare.view.tapestry.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.linagora.linShare.view.tapestry.enums.BusinessUserMessageType;

/** Defines the severity of a message.
 */
public class BusinessUserMessage {

    private BusinessUserMessageType businessUserMessageType;

    private List<String> extras;

    private MessageSeverity severity;

    public BusinessUserMessage(BusinessUserMessageType type, MessageSeverity severity) {
        this.businessUserMessageType = type;
        this.severity = severity;
    }

    public BusinessUserMessage(BusinessUserMessageType type, MessageSeverity severity, List<String> extras) {
        this.businessUserMessageType = type;
        this.severity = severity;
        this.extras = extras;
    }
    
    public BusinessUserMessage(BusinessUserMessageType type, MessageSeverity severity, String... extras) {
        this.businessUserMessageType = type;
        this.severity = severity;
        this.extras = Arrays.asList(extras);
    }

    public BusinessUserMessageType getBusinessUserMessageType() {
        return businessUserMessageType;
    }

    public void setBusinessUserMessageType(BusinessUserMessageType businessUserMessageType) {
        this.businessUserMessageType = businessUserMessageType;
    }

    public MessageSeverity getSeverity() {
        return severity;
    }

    public List<String> getExtras() {
        if (extras == null) {
            extras = new ArrayList<String>();
        }
        return extras;
    }

    public void setExtras(List<String> extras) {
        this.extras = extras;
    }

}
