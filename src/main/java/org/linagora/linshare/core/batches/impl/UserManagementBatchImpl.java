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
package org.linagora.linshare.core.batches.impl;

import org.linagora.linshare.core.batches.UserManagementBatch;
import org.linagora.linshare.core.service.UserService;

/** Batch for user management.
 */
public class UserManagementBatchImpl implements UserManagementBatch {

    public final UserService userService;

    public UserManagementBatchImpl(UserService userService) {
        this.userService = userService;
    }

    /** Find all outdated guest accounts and remove them. */
    public void cleanExpiredGuestAccounts() {
        userService.cleanExpiredGuestAcccounts();
    }

}
