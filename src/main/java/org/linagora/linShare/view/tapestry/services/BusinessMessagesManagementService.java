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
package org.linagora.linShare.view.tapestry.services;

import java.util.List;

import org.apache.tapestry5.ioc.Messages;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;

/** This service manages the business errors.
 */
public interface BusinessMessagesManagementService {

    /** Notify the service that a business exception has occured.
     * @param exception the business exception.
     */
    void notify(BusinessException exception);

    /** Notify the service that a message must be displayed to the user.
     * @param message the business message to display.
     */
    void notify(BusinessUserMessage businessUserMessage);

    /** Get a list of error messages.
     * @return the list of error messages.
     */
    List<String> getBusinessMessages(Messages messages);

    /** Reset the list of error messages. */
    void reset();

    /** Check if error list is empty. */
    boolean isListEmpty();
}
