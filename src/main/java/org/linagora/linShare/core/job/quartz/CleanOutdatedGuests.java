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
package org.linagora.linShare.core.job.quartz;

import org.linagora.linShare.core.batches.UserManagementBatch;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/** This job clean periodically outdated guests.
 */
public class CleanOutdatedGuests extends QuartzJobBean {

    private UserManagementBatch batch;

    public CleanOutdatedGuests() {}

    protected void executeInternal(JobExecutionContext context) {
        batch.cleanExpiredGuestAccounts();
    }

    /** @param userManagementBatch used batch. */
    public void setBatch(UserManagementBatch batch) {
        this.batch = batch;

    }
}