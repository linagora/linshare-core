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
package org.linagora.linshare.core.job.quartz;

import java.util.UUID;

public class BatchRunContext {

	protected final String uuid;

	protected final String asyncTaskUuid;

	protected final String upgradeTaskUuid;

	protected final String actorUuid;

	// null if not used.
	protected String batchHistoryUuid;

	public BatchRunContext(
			String actorUuid,
			String asyncTaskUuid,
			String upgradeTaskUuid) {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.actorUuid = actorUuid;
		this.asyncTaskUuid = asyncTaskUuid;
		this.upgradeTaskUuid = upgradeTaskUuid;
	}

	public BatchRunContext() {
		super();
		this.uuid = UUID.randomUUID().toString();
		this.actorUuid = null;
		this.asyncTaskUuid = null;
		this.upgradeTaskUuid = null;
	}

	public String getUuid() {
		return uuid;
	}

	public String getActorUuid() {
		return actorUuid;
	}

	public String getAsyncTaskUuid() {
		return asyncTaskUuid;
	}

	public String getUpgradeTaskUuid() {
		return upgradeTaskUuid;
	}

	@Override
	public String toString() {
		return "BatchRunContext [uuid=" + uuid + ", asyncTaskUuid=" + asyncTaskUuid + ", upgradeTaskUuid="
				+ upgradeTaskUuid + ", actorUuid=" + actorUuid + ", batchHistoryUuid=" + batchHistoryUuid + "]";
	}

}
