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
package org.linagora.linshare.view.tapestry.components;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;

/** Displays business errors.
 *
 */
public class BusinessMessagesDisplayer {

    @Inject
    private Messages messages;

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

    @Property
    private String errorMessage;

    @Persist
    @Property
    private List<String> errorMessages;

    @SetupRender
    public void init() {
        errorMessages = businessMessagesManagementService.getBusinessMessages(messages);
    }

    public List<String> getBusinessErrors() {
        return errorMessages;
    }

    public boolean isErrorListEmpty() {
        return businessMessagesManagementService.isListEmpty();
    }

    @AfterRender
    public void endRender() {
        errorMessages = new ArrayList<String>();
        businessMessagesManagementService.reset();
    }
}
