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
import java.util.List;

import org.linagora.linShare.core.exception.BusinessException;

/** Stores business error messages.
 */
public class BusinessInformativeContentBundle {

    private List<BusinessException> businessExceptions;

    private List<BusinessUserMessage> businessUserMessages;


    public List<BusinessException> getBusinessExceptions() {
        if (businessExceptions == null) {
            businessExceptions = new ArrayList<BusinessException>();
        }
        return businessExceptions;
    }

    public void setBusinessExceptions(List<BusinessException> businessExceptions) {
        this.businessExceptions = businessExceptions;
    }

    public List<BusinessUserMessage> getBusinessUserMessages() {
        if (businessUserMessages == null) {
            businessUserMessages = new ArrayList<BusinessUserMessage>();
        }
        return businessUserMessages;
    }

    public void setBusinessUserMessages(List<BusinessUserMessage> businessUserMessages) {
        this.businessUserMessages = businessUserMessages;
    }

}
