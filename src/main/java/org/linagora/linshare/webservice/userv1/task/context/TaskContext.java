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
package org.linagora.linshare.webservice.userv1.task.context;

import org.linagora.linshare.core.facade.webservice.common.dto.AccountDto;

public class TaskContext {

	protected final AccountDto authUserDto;

	protected final String actorUuid;

	public TaskContext(AccountDto authUserDto, String actorUuid) {
		super();
		this.authUserDto = authUserDto;
		this.actorUuid = actorUuid;
	}

	public AccountDto getAuthUserDto() {
		return authUserDto;
	}

	public String getActorUuid() {
		return actorUuid;
	}

	@Override
	public String toString() {
		return "TaskContext [actorUuid=" + actorUuid + "]";
	}

}
